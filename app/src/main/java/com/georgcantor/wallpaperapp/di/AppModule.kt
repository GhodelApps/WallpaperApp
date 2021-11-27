package com.georgcantor.wallpaperapp.di

import android.content.Context
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.FavDatabase
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.model.remote.ApiService
import com.georgcantor.wallpaperapp.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context) = FavDatabase.buildDefault(context).dao()

    @Singleton
    @Provides
    fun providesApiClient(@ApplicationContext context: Context) = ApiClient.create(context)

    @Singleton
    @Provides
    fun providesRepository(service: ApiService, dao: FavDao) = Repository(service, dao)
}