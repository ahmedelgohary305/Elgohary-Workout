package com.example.workoutapp.data.local

import com.example.workoutapp.presentation.utils.ExerciseSet

fun SetEntity.toExerciseSet(): ExerciseSet {
    return ExerciseSet(
        kg = kg,
        reps = reps,
        rir = rir
    )
}