package com.acon.core.data.di

import com.acon.core.data.stream.DataStream
import com.acon.core.data.stream.DataStreamImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStreamModule {

    @Singleton
    @Binds
    @VerifiedArea
    abstract fun bindsVerifiedAreaDataStream(
        impl: DataStreamImpl
    ) : DataStream

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VerifiedArea