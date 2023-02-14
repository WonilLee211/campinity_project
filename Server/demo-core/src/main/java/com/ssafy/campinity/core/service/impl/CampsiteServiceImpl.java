package com.ssafy.campinity.core.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.campinity.core.dto.*;
import com.ssafy.campinity.core.entity.campsite.Campsite;
import com.ssafy.campinity.core.entity.campsite.CampsiteImage;
import com.ssafy.campinity.core.entity.campsite.CampsiteScrap;
import com.ssafy.campinity.core.entity.campsite.RegionLocation;
import com.ssafy.campinity.core.entity.member.Member;
import com.ssafy.campinity.core.entity.review.Review;
import com.ssafy.campinity.core.repository.campsite.*;
import com.ssafy.campinity.core.repository.campsite.custom.CampsiteCustomRepository;
import com.ssafy.campinity.core.repository.member.MemberRepository;
import com.ssafy.campinity.core.repository.message.MessageRepository;
import com.ssafy.campinity.core.repository.redis.RedisDao;
import com.ssafy.campinity.core.repository.review.ReviewRepository;
import com.ssafy.campinity.core.service.CampsiteService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CampsiteServiceImpl implements CampsiteService {

    private final CampsiteRepository campsiteRepository;
    private final CampsiteCustomRepository campsiteCustomRepository;
    private final CampsiteScrapRepository campsiteScrapRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final CampsiteImageRepository campsiteImageRepository;
    private final RedisDao redisDao;
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final RegionLocationRepository regionLocationRepository;
    private final CampsiteAndIndustryRepository campsiteAndIndustryRepository;
    private final Logger logger = LogManager.getLogger(CampsiteServiceImpl.class);

    @Override
    @Transactional
    public List<Campsite> getMetaDataListByLatLng(LocationInfoDTO locationInfoDTO) {
        Double topLeftLat = locationInfoDTO.getTopLeftLat();
        Double topLeftLng = locationInfoDTO.getTopLeftLng();
        Double bottomRightLat = locationInfoDTO.getBottomRightLat();
        Double bottomRightLng = locationInfoDTO.getBottomRightLng();

        return campsiteRepository.getCampsitesByLatitudeBetweenAndLongitudeBetween(bottomRightLat, topLeftLat,
                topLeftLng, bottomRightLng);
    }

    @Override
    @Transactional
    public List<CampsiteListResDTO> getCampsitesByLatLng(LocationInfoDTO locationInfoDTO, int memberId) {
        Double topLeftLat = locationInfoDTO.getTopLeftLat();
        Double topLeftLng = locationInfoDTO.getTopLeftLng();
        Double bottomRightLat = locationInfoDTO.getBottomRightLat();
        Double bottomRightLng = locationInfoDTO.getBottomRightLng();

        List<Campsite> campsites = campsiteRepository.getCampsitesByLatitudeBetweenAndLongitudeBetween(bottomRightLat,
                topLeftLat, topLeftLng, bottomRightLng);

        List<CampsiteListResDTO> results = new ArrayList<>();
        for (Campsite camp: campsites) {

            List<String> images = camp.getImages().stream().map(image -> {
                return image.getImagePath();
            }).collect(Collectors.toList());

            List<String> thumbnails = camp.getImages().stream().map(image -> {
                return image.getThumbnailImage();
            }).collect(Collectors.toList());

            boolean isScraped = camp.getScraps().stream().anyMatch(campsiteScrap -> campsiteScrap.getMember().getId() == memberId);

            int messageCnt = camp.getMessages().size();

            results.add(CampsiteListResDTO.builder().camp(camp).isScraped(isScraped)
                    .thumbnails(thumbnails).messageCnt(messageCnt).images(images).build());

            if (results.size() >= 100) {
                break;
            }
        }
        return results;
    }

    @Override
    @Transactional
    public List<ClusteringDoLevelResDTO> getScopeClusteringByDoLevel(LocationInfoDTO locationInfoDTO, int memberId) {
        Double topLeftLat = locationInfoDTO.getTopLeftLat();
        Double topLeftLng = locationInfoDTO.getTopLeftLng();
        Double bottomRightLat = locationInfoDTO.getBottomRightLat();
        Double bottomRightLng = locationInfoDTO.getBottomRightLng();

        List<Campsite> campsites = campsiteRepository.getCampsitesByLatitudeBetweenAndLongitudeBetween(bottomRightLat,
                topLeftLat, topLeftLng, bottomRightLng);

        List<RegionLocation> regions = regionLocationRepository.findBySigunguName("");  // 각 도별 정보 가지고 오기
        List<ClusteringDoLevelResDTO> result = new ArrayList<>();

        HashMap<String, Integer> counts = new HashMap<>();

        for (Campsite camp: campsites) {  // 각 도별 개수 세기
            counts.put(camp.getDoName(), counts.getOrDefault(camp.getDoName(), 0) + 1);
        }

        for (RegionLocation item: regions) {
            result.add(ClusteringDoLevelResDTO.builder().doName(item.getDoName()).latitude(item.getLatitude()).longitude(item.getLongitude()).cnt(counts.getOrDefault(item.getDoName(), 0)).build());
        }

        return result;
    }

    @Override
    @Transactional
    public List<ClusteringSigunguLevelResDTO> getScopeClusteringBySigunguLevel(LocationInfoDTO locationInfoDTO, int memberId) {
        Double topLeftLat = locationInfoDTO.getTopLeftLat();
        Double topLeftLng = locationInfoDTO.getTopLeftLng();
        Double bottomRightLat = locationInfoDTO.getBottomRightLat();
        Double bottomRightLng = locationInfoDTO.getBottomRightLng();

        List<Campsite> campsites = campsiteRepository.getCampsitesByLatitudeBetweenAndLongitudeBetween(bottomRightLat,
                topLeftLat, topLeftLng, bottomRightLng);

        List<ClusteringSigunguLevelResDTO> result = new ArrayList<>();

        HashMap<String, Integer> counts = new HashMap<>();

        List<RegionLocation> regions = regionLocationRepository.findAll();

        for (Campsite camp: campsites) {  // 각 시군구별 개수 세기
            counts.put(camp.getDoName() + "/" + camp.getSigunguName(), counts.getOrDefault(camp.getDoName() + "/" + camp.getSigunguName(), 0) + 1);
        }

        for (RegionLocation region: regions) {
            int cnt = counts.getOrDefault(region.getDoName()+"/"+region.getSigunguName(), 0);
            if (cnt != 0) {
                result.add(ClusteringSigunguLevelResDTO.builder().sigunguName(region.getSigunguName()).latitude(region.getLatitude()).longitude(region.getLongitude()).cnt(cnt).build());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public CampsitePagingResDTO getScopeClusteringByDetailLevel(LocationInfoDTO locationInfoDTO, int memberId, int paging) {
        Double topLeftLat = locationInfoDTO.getTopLeftLat();
        Double topLeftLng = locationInfoDTO.getTopLeftLng();
        Double bottomRightLat = locationInfoDTO.getBottomRightLat();
        Double bottomRightLng = locationInfoDTO.getBottomRightLng();

        List<Campsite> campsites = campsiteRepository.getCampsitesByLatitudeBetweenAndLongitudeBetween(bottomRightLat,
                topLeftLat, topLeftLng, bottomRightLng);

        int start = Math.min(50 * (paging - 1), campsites.size());
        int end = Math.min(start + 50, campsites.size());

        List<CampsiteListResDTO> campsiteListResDTOS = new ArrayList<>();

        for (int i = start; i < end; i++) {
            boolean isScraped = campsites.get(i).getScraps().stream().anyMatch(campsiteScrap -> campsiteScrap.getMember().getId() == memberId);

            int messageCnt = campsites.get(i).getMessages().size();

            List<String> images = campsites.get(i).getImages().stream().map(CampsiteImage::getImagePath).collect(Collectors.toList());

            List<String> thumbnails = campsites.get(i).getImages().stream().map(CampsiteImage::getThumbnailImage).collect(Collectors.toList());

            campsiteListResDTOS.add(CampsiteListResDTO.builder().camp(campsites.get(i)).isScraped(isScraped)
                    .thumbnails(thumbnails).messageCnt(messageCnt).images(images).build());
        }

        return CampsitePagingResDTO.builder().currentPage(paging).maxPage((int)Math.ceil(campsites.size() / 50.0)).data(campsiteListResDTOS).build();
    }

    @Transactional
    @Override
    public List<CampsiteListResDTO> getCampsiteListByFiltering(String keyword, String doName, String[] sigunguNames,
                                                               String[] fclties, String[] amenities, String[] industries,
                                                               String[] themes, String[] allowAnimals, String[] openSeasons,
                                                               int memberId) {



        List<Campsite> campsites = campsiteCustomRepository.getCampsiteListByFiltering(keyword, doName, sigunguNames,
                fclties, amenities, industries, themes, allowAnimals, openSeasons);

        List<CampsiteListResDTO> results = campsites.stream().map(camp -> {
            boolean isScraped = camp.getScraps().stream().anyMatch(campsiteScrap -> campsiteScrap.getMember().getId() == memberId);

            int messageCnt = camp.getMessages().size();

            List<String> images = camp.getImages().stream().map(image -> {
                return image.getImagePath();
            }).collect(Collectors.toList());

            List<String> thumbnails = camp.getImages().stream().map(image -> {
                return image.getThumbnailImage();
            }).collect(Collectors.toList());

            return CampsiteListResDTO.builder().camp(camp).isScraped(isScraped)
                    .thumbnails(thumbnails).messageCnt(messageCnt).images(images).build();
        }).collect(Collectors.toList());

        return results;
    }

    // do level 반환
    @Override
    @Transactional
    public List<ClusteringDoLevelResDTO> getCampsiteClusteringByDoLevel(String keyword, String doName, String[] sigunguNames, String[] fclties, String[] amenities, String[] industries, String[] themes, String[] allowAnimals, String[] openSeasons, int memberId) {
        List<Campsite> campsites = campsiteCustomRepository.getCampsiteListByFiltering(keyword, doName, sigunguNames,
                fclties, amenities, industries, themes, allowAnimals, openSeasons);

        List<RegionLocation> regions = regionLocationRepository.findBySigunguName("");  // 각 도별 정보 가지고 오기
        List<ClusteringDoLevelResDTO> result = new ArrayList<>();

        HashMap<String, Integer> counts = new HashMap<>();

        for (Campsite camp: campsites) {  // 각 도별 개수 세기
            counts.put(camp.getDoName(), counts.getOrDefault(camp.getDoName(), 0) + 1);
        }

        for (RegionLocation item: regions) {
            result.add(ClusteringDoLevelResDTO.builder().doName(item.getDoName()).latitude(item.getLatitude()).longitude(item.getLongitude()).cnt(counts.getOrDefault(item.getDoName(), 0)).build());
        }

        return result;
    }

    // sigungn level 반환
    @Override
    @Transactional
    public List<ClusteringSigunguLevelResDTO> getCampsiteClusteringBySigunguLevel(String keyword, String doName, String[] sigunguNames, String[] fclties, String[] amenities, String[] industries, String[] themes, String[] allowAnimals, String[] openSeasons, int memberId) {
        List<Campsite> campsites = campsiteCustomRepository.getCampsiteListByFiltering(keyword, doName, sigunguNames,
                fclties, amenities, industries, themes, allowAnimals, openSeasons);

        List<ClusteringSigunguLevelResDTO> result = new ArrayList<>();

        List<RegionLocation> regions = regionLocationRepository.findAll();

        HashMap<String, Integer> counts = new HashMap<>();

        for (Campsite camp: campsites) {  // 각 시군구별 개수 세기
            counts.put(camp.getDoName() + "/" + camp.getSigunguName(), counts.getOrDefault(camp.getDoName() + "/" + camp.getSigunguName(), 0) + 1);
        }

        for (RegionLocation region: regions) {
            int cnt = counts.getOrDefault(region.getDoName()+"/"+region.getSigunguName(), 0);
            if (cnt != 0) {
                result.add(ClusteringSigunguLevelResDTO.builder().sigunguName(region.getSigunguName()).latitude(region.getLatitude()).longitude(region.getLongitude()).cnt(cnt).build());
            }
        }
        return result;
    }

    // detail반환
    @Override
    @Transactional
    public CampsitePagingResDTO getCampsiteClusteringByDetailLevel(String keyword, String doName, String[] sigunguNames, String[] fclties, String[] amenities, String[] industries, String[] themes, String[] allowAnimals, String[] openSeasons, int memberId, int paging) {
        List<Campsite> campsites = campsiteCustomRepository.getCampsiteListByFiltering(keyword, doName, sigunguNames,
                fclties, amenities, industries, themes, allowAnimals, openSeasons);

        int start = Math.min(50 * (paging - 1), campsites.size());
        int end = Math.min(start + 50, campsites.size());

        List<CampsiteListResDTO> campsiteListResDTOS = new ArrayList<>();

        for (int i = start; i < end; i++) {
            boolean isScraped = campsites.get(i).getScraps().stream().anyMatch(campsiteScrap -> campsiteScrap.getMember().getId() == memberId);

            int messageCnt = campsites.get(i).getMessages().size();

            List<String> images = campsites.get(i).getImages().stream().map(CampsiteImage::getImagePath).collect(Collectors.toList());

            List<String> thumbnails = campsites.get(i).getImages().stream().map(CampsiteImage::getThumbnailImage).collect(Collectors.toList());

            campsiteListResDTOS.add(CampsiteListResDTO.builder().camp(campsites.get(i)).isScraped(isScraped)
                    .thumbnails(thumbnails).messageCnt(messageCnt).images(images).build());
        }

        return CampsitePagingResDTO.builder().currentPage(paging).maxPage((int)Math.ceil(campsites.size() / 50.0)).data(campsiteListResDTOS).build();
    }

    @Transactional
    @Override
    public CampsiteListResDTO getCampsiteMetaData(UUID campsiteId, int memberId) {
        Campsite camp = campsiteRepository.findByUuid(campsiteId).orElseThrow(IllegalAccessError::new);

        List<String> images = camp.getImages().stream().map(image -> {
            return image.getImagePath();
        }).collect(Collectors.toList());

        List<String> thumbnails = camp.getImages().stream().map(image -> {
            return image.getThumbnailImage();
        }).collect(Collectors.toList());

        boolean isScraped = camp.getScraps().stream().anyMatch(campsiteScrap -> campsiteScrap.getMember().getId() == memberId);

        int messageCnt = camp.getMessages().size();

        return CampsiteListResDTO.builder().camp(camp).images(images).isScraped(isScraped)
                .thumbnails(thumbnails).messageCnt(messageCnt).build();
    }

    @Override
    @Transactional
    public Boolean scrap(int memberId, UUID campsiteId) {
        Campsite campsite = campsiteRepository.findByUuid(campsiteId).orElseThrow(IllegalArgumentException::new);
        Member member = memberRepository.findMemberByIdAndExpiredIsFalse(memberId).orElseThrow(IllegalArgumentException::new);

        Optional<CampsiteScrap> campsiteScrap = campsiteScrapRepository.findByMember_idAndCampsite_id(member.getId(), campsite.getId());

        if (campsiteScrap.isPresent()) {
            campsiteScrapRepository.delete(campsiteScrap.get());
            campsite.removeCampsiteScrap(campsiteScrap.get());
            return false;
        } else {
            CampsiteScrap saved = campsiteScrapRepository.save(CampsiteScrap.builder().campsite(campsite).member(member).build());
            campsite.addCampsiteScrap(saved);
            return true;
        }
    }

    @Override
    @Transactional
    public CampsiteDetailResDTO getCampsiteDetail(UUID campsiteId, int memberId) {
        Campsite campsite = campsiteRepository.findByUuid(campsiteId).orElseThrow(IllegalArgumentException::new);
        Member member = memberRepository.findMemberByIdAndExpiredIsFalse(memberId).orElseThrow(IllegalArgumentException::new);

        List<Review> reviews = reviewRepository.findByCampsite_idAndExpiredIsFalseOrderByCreatedAtDesc(campsite.getId());

        List<ReviewResDTO> reviewDTOLists = reviews.stream().map(review -> ReviewResDTO.builder().review(review).build()).collect(Collectors.toList());

        List<String> images = campsiteImageRepository.findByCampsite_id(campsite.getId()).stream().map(image -> {
            return image.getImagePath();
        }).collect(Collectors.toList());

        List<String> thumbnail_images = campsiteImageRepository.findByCampsite_id(campsite.getId()).stream().map(image -> {
            return image.getThumbnailImage();
        }).collect(Collectors.toList());

        return CampsiteDetailResDTO.builder().camp(campsite).member(member).images(images).reviews(reviewDTOLists).thumbnail_images(thumbnail_images).build();
    }

    @Override
    public List<CampsiteMetaResDTO> getCampsiteScrapList(int memberId) {
        List<Campsite> campsites = campsiteRepository.findCampsiteScrapList(memberId);
        return campsites.stream().map(campsite -> {
            return CampsiteMetaResDTO.builder().campsite(campsite).isScraped(true).build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<Campsite> getCampsiteByCampName(String keyword) {
        return campsiteRepository.findByCampNameContains(keyword);
    }

    @Override
    public List<CampsiteRankingResDTO> getHottestCampsite() throws JsonProcessingException {
        String highestCampsite = redisDao.getValues("hottestCampsite");
        return objectMapper.readValue(highestCampsite, new TypeReference<List<CampsiteRankingResDTO>>(){});
    }

    @Override
    public List<CampsiteRankingResDTO> getHighestCampsite() throws JsonProcessingException {
        String highestCampsite = redisDao.getValues("highestCampsite");
        return objectMapper.readValue(highestCampsite, new TypeReference<List<CampsiteRankingResDTO>>(){});
    }
}
