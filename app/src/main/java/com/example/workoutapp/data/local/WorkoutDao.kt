package com.example.workoutapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>)

    @Transaction
    @Query("""
    SELECT sets.* FROM sets
    INNER JOIN exercises ON sets.exerciseId = exercises.exerciseId
    INNER JOIN workouts ON exercises.workoutId = workouts.workoutId
    WHERE exercises.name = :exerciseName 
    AND workouts.date = (
        SELECT MAX(date) FROM workouts
        INNER JOIN exercises ON workouts.workoutId = exercises.workoutId
        WHERE exercises.name = :exerciseName
    )
    ORDER BY sets.setId ASC
""")
    fun getAllExerciseSets(exerciseName: String): Flow<List<SetEntity>>

    @Transaction
    suspend fun insertWorkoutWithExercises(
        workout: WorkoutEntity,
        exercises: List<ExerciseWithSets>
    ) {
        val workoutId = insertWorkout(workout)

        val exerciseEntities = exercises.map {
            ExerciseEntity(
                name = it.exercise.name,
                workoutId = workoutId.toInt(),
                note = it.exercise.note
            )
        }
        val exerciseIds = insertExercises(exerciseEntities)

        val sets = exercises.flatMapIndexed { index, exerciseWithSets ->
            exerciseWithSets.sets.map { set ->
                set.copy(exerciseId = exerciseIds[index].toInt())
            }
        }

        insertSets(sets)
    }

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercisesAndSets>>

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
}