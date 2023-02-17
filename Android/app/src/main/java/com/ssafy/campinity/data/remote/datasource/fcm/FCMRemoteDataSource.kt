package com.ssafy.campinity.data.remote.datasource.fcm

interface FCMRemoteDataSource {

    suspend fun renewToken(fcmToken: String): FCMTokenResponse

    suspend fun subscribeCampsite(body: FCMTokenRequest): FCMTokenResponse

    suspend fun createHelpNote(body: FCMMessageRequest): Int

    suspend fun replyHelp(body: FCMReplyRequest): Int?
}