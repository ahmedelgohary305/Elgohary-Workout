package com.example.workoutapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workoutapp.presentation.screens.ExercisesScreen
import com.example.workoutapp.presentation.screens.WorkoutAddScreen
import com.example.workoutapp.presentation.screens.WorkoutListScreen
import com.example.workoutapp.presentation.utils.Routes
import com.example.workoutapp.presentation.viewmodels.ExerciseViewModel
import com.example.workoutapp.presentation.viewmodels.WorkoutViewModel

@Composable
fun WorkoutNavGraph(
    context: Context
) {
    val navController = rememberNavController()
    val workoutViewModel = hiltViewModel<WorkoutViewModel>()
    val exerciseViewModel = hiltViewModel<ExerciseViewModel>()

    NavHost(
        navController = navController,
        startDestination = Routes.WorkoutListScreen.route
    ) {
        composable(Routes.WorkoutListScreen.route) {
            WorkoutListScreen(
                workoutViewModel = workoutViewModel,
                exerciseViewModel = exerciseViewModel,
                navController = navController
            )
        }

        composable(Routes.WorkoutAddScreen.route) {
            WorkoutAddScreen(
                navController = navController,
                exerciseViewModel = exerciseViewModel,
                workoutViewModel = workoutViewModel,
                context = context
            )
        }

        composable(Routes.WorkoutExercisesScreen.route) {
            ExercisesScreen(
                exerciseViewModel = exerciseViewModel,
                workoutViewModel = workoutViewModel,
                navController = navController,
                context = context
            )
        }

    }

}