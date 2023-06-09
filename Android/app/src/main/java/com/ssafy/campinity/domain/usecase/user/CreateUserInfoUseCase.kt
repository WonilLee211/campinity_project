package com.ssafy.campinity.domain.usecase.user

import com.ssafy.campinity.data.remote.Resource
import com.ssafy.campinity.domain.entity.user.User
import com.ssafy.campinity.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun createUserInfo(
        nickname: String,
        profileImg: MultipartBody.Part?,
        fcmToken: String
    ): Resource<User> = withContext(Dispatchers.IO) {
        userRepository.createUserInfo(nickname, profileImg, fcmToken)
    }

    suspend fun createUserInfoWithoutImg(
        nickname: String,
        fcmToken: String
    ): Resource<User> = withContext(Dispatchers.IO) {
        userRepository.createUserInfoWithoutImg(nickname, fcmToken)
    }
}