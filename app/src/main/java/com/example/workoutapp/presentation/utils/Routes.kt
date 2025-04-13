package com.example.workoutapp.presentation.utils

sealed class Routes(val route: String) {
    object WorkoutListScreen: Routes("workout_list_screen")
    object WorkoutAddScreen: Routes("workout_add_screen")
    object WorkoutExercisesScreen: Routes("workout_exercises_screen")
}