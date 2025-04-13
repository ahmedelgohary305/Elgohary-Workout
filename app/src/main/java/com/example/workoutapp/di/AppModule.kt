package com.example.workoutapp.di


import android.content.Context
import androidx.room.Room
import com.example.workoutapp.data.local.WorkoutDatabase
import com.example.workoutapp.data.local.WorkoutRepoImpl
import com.example.workoutapp.domain.WorkoutRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WorkoutDatabase {
        return Room.databaseBuilder(
            context,
            WorkoutDatabase::class.java,
            "workout_db"
        ).fallbackToDestructiveMigration().build()
    }


    @Provides
    @Singleton
    fun provideWorkoutRepo(database: WorkoutDatabase): WorkoutRepo {
        return WorkoutRepoImpl(database)
    }
}