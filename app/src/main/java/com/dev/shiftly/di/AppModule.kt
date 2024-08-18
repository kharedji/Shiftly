package com.dev.shiftly.di

import com.dev.shiftly.data.repository.UserRepositoryImpl
import com.dev.shiftly.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideSignUpRepository(): UserRepository {
        return UserRepositoryImpl()
    }
}