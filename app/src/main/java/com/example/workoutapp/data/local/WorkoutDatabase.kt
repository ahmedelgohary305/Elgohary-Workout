package com.example.workoutapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        SetEntity::class,
        CreatedExerciseEntity::class
    ],
    version = 21,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract val workoutDao: WorkoutDao
    abstract val createdExerciseDao: CreatedExerciseDao
}