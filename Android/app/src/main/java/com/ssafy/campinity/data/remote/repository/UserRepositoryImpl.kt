package com.ssafy.campinity.data.remote.repository

import com.ssafy.campinity.common.util.wrapToResource
import com.ssafy.campinity.data.remote.Resource
import com.ssafy.campinity.data.remote.datasource.user.UserRemoteDataSource
import com.ssafy.campinity.domain.entity.user.User
import com.ssafy.campinity.domain.entity.user.UserProfile
import com.ssafy.campinity.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun createUserInfo(
        nickName: String,
        profileImg: MultipartBody.Part?,
        fcmToken: String
    ): Resource<User> =
        wrapToResource(Dispatchers.IO) {
            userRemoteDataSource.createUserInfo(nickName, profileImg, fcmToken).toDomainModel()
        }

    override suspend fun createUserInfoWithoutImg(
        nickName: String,
        fcmToken: String
    ): Resource<User> =
        wrapToResource(Dispatchers.IO) {
            userRemoteDataSource.createUserInfoWithoutImg(nickName, fcmToken).toDomainModel()
        }

    override suspend fun checkDuplication(nickName: String): Resource<Boolean> =
        wrapToResource(Dispatchers.IO) {
            userRemoteDataSource.checkDuplication(nickName)
        }

    override suspend fun cancelSignUp(): Resource<Boolean> =
        wrapToResource(Dispatchers.IO) {
            userRemoteDataSource.cancelSignUp()
        }

    override suspend fun getUserProfile(): Resource<UserProfile> =
        wrapToResource(Dispatchers.IO){
            userRemoteDataSource.getUserProfileImg().toDomainModel()
        }
}