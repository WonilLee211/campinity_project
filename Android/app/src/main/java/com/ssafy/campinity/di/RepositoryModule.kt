package com.ssafy.campinity.di

import com.ssafy.campinity.data.remote.datasource.CommunityCampsite.CommunityRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.auth.AuthRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.collection.CollectionRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.curation.CurationRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.home.HomeRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.user.UserRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.datasource.note.NoteRemoteDataSourceImpl
import com.ssafy.campinity.data.remote.repository.*
import com.ssafy.campinity.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRepository = AuthRepositoryImpl(authRemoteDataSourceImpl)

    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSourceImpl: UserRemoteDataSourceImpl
    ): UserRepository = UserRepositoryImpl(userRemoteDataSourceImpl)

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteRemoteDataSourceImpl: NoteRemoteDataSourceImpl
    ): NoteRepository = NoteRepositoryImpl(noteRemoteDataSourceImpl)

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionRemoteDataSourceImpl: CollectionRemoteDataSourceImpl
    ): CollectionRepository = CollectionRepositoryImpl(collectionRemoteDataSourceImpl)

    @Provides
    @Singleton
    fun provideCurationRepository(
        curationRemoteDataSourceImpl: CurationRemoteDataSourceImpl
    ): CurationRepository = CurationRepositoryImpl(curationRemoteDataSourceImpl)

    @Provides
    @Singleton
    fun provideCommunityRepository(
        communityRemoteDataSourceImpl: CommunityRemoteDataSourceImpl
    ): CommunityRepository = CommunityRepositoryImpl(communityRemoteDataSourceImpl)

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeRemoteDataSourceImpl: HomeRemoteDataSourceImpl
    ): HomeRepository = HomeRepositoryImpl(homeRemoteDataSourceImpl)
}
