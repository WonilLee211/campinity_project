package com.ssafy.campinity.domain.repository

import com.ssafy.campinity.data.remote.Resource
import com.ssafy.campinity.domain.entity.collection.CollectionItem

interface CollectionRepository {

    suspend fun getCollections(): Resource<List<CollectionItem>>
}