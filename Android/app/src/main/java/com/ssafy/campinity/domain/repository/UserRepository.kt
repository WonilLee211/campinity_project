package com.ssafy.campinity.domain.repository

import com.ssafy.campinity.data.remote.Resource
import com.ssafy.campinity.domain.entity.user.User
import okhttp3.MultipartBody

interface UserRepository {

    suspend fun editUserInfo(nickName: String, profileImg: MultipartBody.Part?, fcmToken: String): Resource<User>

    suspend fun editUserInfoWithoutImg(nickName: String, fcmToken: String): Resource<User>

    suspend fun checkDuplication(nickName: String): Resource<Boolean>

    suspend fun cancelSignUp(): Resource<Boolean>

}