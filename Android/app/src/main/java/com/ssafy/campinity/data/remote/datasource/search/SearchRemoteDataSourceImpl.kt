package com.ssafy.campinity.data.remote.datasource.search

import com.ssafy.campinity.data.remote.datasource.note.NoteBriefResponse
import com.ssafy.campinity.data.remote.service.SearchApiService
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val searchApiService: SearchApiService
) : SearchRemoteDataSource {

    override suspend fun getCampsitesByFiltering(filter: SearchFilterRequest): List<SearchBriefResponse> =
        searchApiService.getCampsitesByFiltering(
            filter.allowAnimal,
            filter.amenity,
            filter.doName,
            filter.fclty,
            filter.industry,
            filter.keyword,
            filter.openSeason,
            filter.sigunguName,
            filter.theme
        )

    override suspend fun getCampsitesByScope(
        bottomRightLat: Double,
        bottomRightLng: Double,
        topLeftLat: Double,
        topLeftLng: Double
    ): List<SearchBriefResponse> = searchApiService.getCampsitesByScope(
        bottomRightLat,
        bottomRightLng,
        topLeftLat,
        topLeftLng
    )

    override suspend fun getCampsiteDetail(campsiteId: String): SearchDetailResponse =
        searchApiService.getCampsiteDetail(campsiteId)

    override suspend fun getCampsiteReviewNotes(
        campsiteId: String,
        bottomRightLat: Double,
        bottomRightLng: Double,
        topLeftLat: Double,
        topLeftLng: Double,
    ): List<NoteBriefResponse> = searchApiService.getCampsiteReviewNotes(
        campsiteId,
        bottomRightLat,
        bottomRightLng,
        topLeftLat,
        topLeftLng,
    )

    override suspend fun writeReview(review: SearchReviewRequest): SearchReviewResponse =
        searchApiService.writeReview(review)

    override suspend fun deleteReview(reviewId: String): SearchDeleteReviewResponse =
        searchApiService.deleteReview(reviewId)

    override suspend fun scrapCampsite(campsiteId: String): SearchScrapResponse =
        searchApiService.scrapCampsite(campsiteId)
}