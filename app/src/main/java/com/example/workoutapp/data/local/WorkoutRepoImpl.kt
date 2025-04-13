package com.example.workoutapp.data.local

import com.example.workoutapp.domain.WorkoutRepo
import com.example.workoutapp.presentation.utils.ExerciseSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class WorkoutRepoImpl @Inject constructor(
    private val database: WorkoutDatabase
): WorkoutRepo {

    override fun getAllWorkouts(): Flow<List<WorkoutWithExercisesAndSets>> {
        return database.workoutDao.getAllWorkoutsWithExercises()
    }

   override suspend fun insertWorkoutWithExercises(workout: WorkoutEntity, exercises: List<ExerciseWithSets>) {
        database.workoutDao.insertWorkoutWithExercises(workout, exercises)
    }

    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        database.workoutDao.deleteWorkout(workout)
    }

    override suspend fun upsertCreatedExercise(createdExercise: CreatedExerciseEntity) {
        return database.createdExerciseDao.upsertCreatedExercise(createdExercise)
    }

    override suspend fun upsertCreatedExercises(createdExercises: List<CreatedExerciseEntity>) {
        if (database.createdExerciseDao.createdExercisesCount() == 0) {
            database.createdExerciseDao.upsertCreatedExercises(createdExercises)
        }
    }

    override fun getAllCreatedExercises(): Flow<List<CreatedExerciseEntity>> {
        return database.createdExerciseDao.getAllCreatedExercises()
    }

    override fun getAllExerciseStats(exerciseName: String): Flow<List<ExerciseSet>> {
        return database.workoutDao.getAllExerciseSets(exerciseName).map { setEntities ->
            setEntities.map { setEntity ->
                ExerciseSet(kg = setEntity.kg, reps = setEntity.reps, rir = setEntity.rir)
            }
        }
    }
}