package com.example.workoutapp.domain


import com.example.workoutapp.data.local.CreatedExerciseEntity
import com.example.workoutapp.data.local.ExerciseWithSets
import com.example.workoutapp.data.local.WorkoutEntity
import com.example.workoutapp.data.local.WorkoutWithExercisesAndSets
import com.example.workoutapp.presentation.utils.ExerciseSet
import kotlinx.coroutines.flow.Flow

interface WorkoutRepo {
    fun getAllWorkouts(): Flow<List<WorkoutWithExercisesAndSets>>
    suspend fun insertWorkoutWithExercises(workout: WorkoutEntity, exercises: List<ExerciseWithSets>)
    suspend fun deleteWorkout(workout: WorkoutEntity)
    suspend fun upsertCreatedExercise(createdExercise: CreatedExerciseEntity)
    suspend fun upsertCreatedExercises(createdExercises: List<CreatedExerciseEntity>)
    fun getAllCreatedExercises(): Flow<List<CreatedExerciseEntity>>
    fun getAllExerciseStats(exerciseName: String): Flow<List<ExerciseSet>>
}