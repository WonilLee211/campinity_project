package com.ssafy.campinity.domain.repository

import com.ssafy.campinity.data.remote.Resource
import com.ssafy.campinity.data.remote.datasource.search.SearchFilterRequest
import com.ssafy.campinity.data.remote.datasource.search.SearchReviewRequest
import com.ssafy.campinity.domain.entity.search.*
import com.ssafy.campinity.data.remote.datasource.search.*
import com.ssafy.campinity.domain.entity.search.*

interface SearchRepository {

    suspend fun getCampsitesByFiltering(filter: SearchFilterRequest): Resource<CampsiteBriefInfoPaging>

    suspend fun getCampsitesByScope(
        bottomRightLat: Double,
        bottomRightLng: Double,
        topLeftLat: Double,
        topLeftLng: Double,
        paging: Int
    ): Resource<CampsiteBriefInfoPaging>

    suspend fun getCampsiteDetail(campsiteId: String): Resource<CampsiteDetailInfo>

    suspend fun getCampsiteReviewNotes(
        campsiteId: String,
        bottomRightLat: Double,
        bottomRightLng: Double,
        topLeftLat: Double,
        topLeftLng: Double,
    ): Resource<List<CampsiteNoteBriefInfo>>

    suspend fun writeReview(review: SearchReviewRequest): Resource<Review>

    suspend fun deleteReview(reviewId: String): Resource<String>

    suspend fun scrapCampsite(campsiteId: String): Resource<Boolean>

    suspend fun getCampsitesByDo(filter: SearchFilterClusteringRequest): Resource<List<ClusteringDo>>

    suspend fun getCampsitesBySiGunGu(filter: SearchFilterClusteringRequest): Resource<List<ClusteringSiGunGu>>
}