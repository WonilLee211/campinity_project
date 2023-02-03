package com.ssafy.campinity.data.remote.datasource.CommunityCampsite

data class CommunityCampsiteMessageResponse(
    val authorName: String,
    val campsiteName: String,
    val content: String,
    val countLikes: Int,
    val createdAt: String,
    val etcValidDate: String,
    val imagePath: String,
    val latitude: String,
    val likeCheck: Boolean,
    val longitude: String,
    val messageCategory: String,
    val messageId: String,
    val updatedAt: String
)
