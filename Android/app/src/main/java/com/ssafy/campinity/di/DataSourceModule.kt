package com.ssafy.campinity.di

import com.ssafy.campinity.data.remote.datasource.CommunityCampsite.CommunityRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.auth.AuthRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.collection.CollectionRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.curation.CurationRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.home.HomeRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.note.NoteRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.search.SearchRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.user.UserRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.service.AuthApiService
import com.ssafy.campinity.data.remote.service.CollectionApiService
import com.ssafy.campinity.data.remote.service.SearchApiService
import com.ssafy.campinity.data.remote.service.UserApiService
import com.ssafy.campinity.data.remote.service.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(
        authApiService: AuthApiService,
        refreshApiService: RefreshApiService
    ): AuthRemoteDataSourceImpl = AuthRemoteDataSourceImpl(authApiService, refreshApiService)

    @Provides
    @Singleton
    fun provideUserDataSource(
        userApiService: UserApiService
    ): UserRemoteDataSourceImpl = UserRemoteDataSourceImpl(userApiService)

    @Provides
    @Singleton
    fun provideNoteDataSource(
        noteApiService: NoteApiService
    ): NoteRemoteDataSourceImpl = NoteRemoteDataSourceImpl(noteApiService)

    @Provides
    @Singleton
    fun provideCollectionDataSource(
        collectionApiService: CollectionApiService
    ): CollectionRemoteDataSourceImpl = CollectionRemoteDataSourceImpl(collectionApiService)

    @Provides
    @Singleton
    fun provideSearchDataSource(
        searchApiService: SearchApiService
    ): SearchRemoteDataSourceImpl = SearchRemoteDataSourceImpl(searchApiService)

    @Provides
    @Singleton
    fun provideCurationDataSource(
        curationApiService: CurationApiService
    ): CurationRemoteDataSourceImpl = CurationRemoteDataSourceImpl(curationApiService)

    @Provides
    @Singleton
    fun provideCommunityDataSource(
        communityApiService: CommunityApiService
    ): CommunityRemoteDataSourceImpl = CommunityRemoteDataSourceImpl(communityApiService)

    @Provides
    @Singleton
    fun provideHomeDataSource(
        homeApiService: HomeApiService
    ): HomeRemoteDataSourceImpl = HomeRemoteDataSourceImpl(homeApiService)
}