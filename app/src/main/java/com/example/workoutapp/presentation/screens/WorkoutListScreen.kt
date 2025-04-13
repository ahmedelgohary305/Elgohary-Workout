package com.example.workoutapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.workoutapp.R
import com.example.workoutapp.data.local.toExerciseSet
import com.example.workoutapp.presentation.utils.Routes
import com.example.workoutapp.presentation.utils.WorkoutItem
import com.example.workoutapp.presentation.viewmodels.ExerciseViewModel
import com.example.workoutapp.presentation.viewmodels.WorkoutViewModel

@Composable
fun WorkoutListScreen(
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    navController: NavController
) {
    val workouts = workoutViewModel.workouts.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
            Image(
                painter = if (isSystemInDarkTheme()) painterResource(R.drawable.logo_png) else painterResource(R.drawable.logo_black_png),
                contentDescription = null,
                modifier = Modifier.size(width = 150.dp, height = 50.dp)
            )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, start = 32.dp, end = 32.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = { navController.navigate(Routes.WorkoutAddScreen.route) },
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_add_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(26.dp)
            )
            Text(
                text = "Start a Workout",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily(Font(R.font.poppins_bold))
            )
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp, top = 8.dp))

        if (workouts?.isNotEmpty() ?: return@Column) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(workouts) { workout ->
                    WorkoutItem(
                        workout = workout,
                        onItemDeleteClick = {
                            exerciseViewModel.clearSelectedExercises()
                            workoutViewModel.deleteWorkout(workout.workout)
                        },
                        onStartWorkoutClick = { workout ->
                            workoutViewModel.setWorkoutName(workout.workout.name)
                            exerciseViewModel.startWorkout(
                                workout.exercises.map { it.exercise.name },
                                workout.exercises.map { it.sets }
                                    .map { it.map { set -> set.toExerciseSet() } },
                            )
                            navController.navigate(Routes.WorkoutAddScreen.route)
                        }
                    )
                }
            }
        } else {
            Text(
                text = "No workouts yet",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                fontFamily = FontFamily(Font(R.font.poppins_bold))
            )
        }
    }
}