package com.example.workoutapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface CreatedExerciseDao {

    @Query("SELECT COUNT(*) FROM created_exercises")
    suspend fun createdExercisesCount(): Int

    @Upsert
    suspend fun upsertCreatedExercise(createdExercise: CreatedExerciseEntity)

    @Upsert
    suspend fun upsertCreatedExercises(createdExercises: List<CreatedExerciseEntity>)

    @Query("Select * from created_exercises")
    fun getAllCreatedExercises(): Flow<List<CreatedExerciseEntity>>

}