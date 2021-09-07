package com.raveendran.jsntest.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.raveendran.jsntest.api.Api
import com.raveendran.jsntest.db.PeopleDao
import com.raveendran.jsntest.db.PeopleDatabase
import com.raveendran.jsntest.repository.PeopleRepository
import com.raveendran.jsntest.util.Constants.database_name
import com.raveendran.jsntest.util.Constants.sharedPreferences_name
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
    fun providePhotoDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        PeopleDatabase::class.java,
        database_name
    )
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()

    @Singleton
    @Provides
    fun providePhotoDao(
        database: PeopleDatabase
    ) = database.peopleDao()

    @Singleton
    @Provides
    fun provideRepository(
        dao: PeopleDao, api: Api
    ): PeopleRepository {
        return PeopleRepository(dao, api)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(sharedPreferences_name, Context.MODE_PRIVATE)

}