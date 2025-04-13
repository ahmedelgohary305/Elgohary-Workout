package com.example.workoutapp.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.workoutapp.R
import com.example.workoutapp.presentation.utils.ExerciseItem
import com.example.workoutapp.presentation.utils.Routes
import com.example.workoutapp.presentation.viewmodels.AddViewModel
import com.example.workoutapp.presentation.viewmodels.ExerciseViewModel
import com.example.workoutapp.presentation.viewmodels.WorkoutViewModel

@Composable
fun WorkoutAddScreen(
    navController: NavController,
    exerciseViewModel: ExerciseViewModel,
    workoutViewModel: WorkoutViewModel,
    context: Context
) {
    val addViewModel = hiltViewModel<AddViewModel>()
    val elapsedTime = addViewModel.elapsedTime.collectAsStateWithLifecycle().value
    val formattedTime = addViewModel.formatTime(elapsedTime)
    val exerciseSets = exerciseViewModel.selectedExerciseSets.collectAsStateWithLifecycle().value
    val selectedExercises = exerciseViewModel.selectedExercises.collectAsStateWithLifecycle().value
    var workoutName = workoutViewModel.workoutName.collectAsStateWithLifecycle().value
    val isAddingNote = rememberSaveable {
        mutableStateOf(false)
    }
    val note = rememberSaveable {
        mutableStateOf("")
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    var showFinishDialog by rememberSaveable { mutableStateOf(false) }
    var showUnfinishedSetsDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(0.6f))

            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_access_time_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(25.dp)
                )
            }

            Text(
                formattedTime,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    if (exerciseSets.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please complete at least one set in order to finish workout",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        if (exerciseViewModel.compareExerciseSetsAndSelected()) {
                            showFinishDialog = true
                        } else {
                            showUnfinishedSetsDialog = true
                        }
                    }
                }
            ) {
                Text(
                    "Finish",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
            }
        }
        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = workoutName,
                onValueChange = { workoutViewModel.setWorkoutName(it) },
                modifier = Modifier
                    .wrapContentSize(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_bold))
                ),
                singleLine = true
            )

            Box {
                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_more_vert_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (isAddingNote.value) {
                        DropdownMenuItem(
                            text = { Text("Remove Note") },
                            onClick = {
                                expanded = false
                                isAddingNote.value = false
                            },
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Add Note") },
                            onClick = {
                                expanded = false
                                isAddingNote.value = true
                            }
                        )
                    }
                }

            }
        }

        if (showFinishDialog) {
            AlertDialog(
                onDismissRequest = { showFinishDialog = false },
                title = {},
                text = {
                    Text(
                        "Finish workout?",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            workoutViewModel.insertWorkout(
                                workoutName,
                                note.value,
                                formattedTime,
                                exerciseSets,
                                exerciseViewModel.exerciseNotes
                            )
                            exerciseViewModel.clearSelectedExercises()
                            addViewModel.stopTimer()
                            navController.popBackStack()
                            showFinishDialog = false
                        }
                    ) {
                        Text(
                            "Yes",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showFinishDialog = false
                        }
                    ) {
                        Text(
                            "No",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                }
            )
        }

        if (showUnfinishedSetsDialog) {
            AlertDialog(
                onDismissRequest = { showUnfinishedSetsDialog = false },
                title = {},
                text = {
                    Text(
                        "There is unselected sets, Are you sure you want to finish?",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            workoutViewModel.insertWorkout(
                                workoutName,
                                note.value,
                                formattedTime,
                                exerciseSets,
                                exerciseViewModel.exerciseNotes
                            )

                            exerciseViewModel.clearSelectedExercises()
                            addViewModel.stopTimer()
                            navController.popBackStack()
                            showUnfinishedSetsDialog = false
                        }
                    ) {
                        Text(
                            "Yes",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showUnfinishedSetsDialog = false
                        }
                    ) {
                        Text(
                            "No",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                }
            )
        }


        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = {
                    Text(
                        "Cancel Workout",
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to cancel this workout?",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            addViewModel.stopTimer()
                            exerciseViewModel.clearSelectedExercises()
                            navController.popBackStack()
                            showCancelDialog = false
                        }
                    ) {
                        Text(
                            "Yes",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCancelDialog = false
                        }
                    ) {
                        Text(
                            "No",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                }
            )
        }

        if (isAddingNote.value) {
            TextField(
                value = note.value,
                onValueChange = { note.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.3f)
                ),
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        text = "Your Note",
                        fontFamily = FontFamily(Font(R.font.poppins_regular))
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    isAddingNote.value = false
                }
            )
        }

        if (selectedExercises.isEmpty()) {
            Text(
                text = "No Exercises Selected",
                modifier = Modifier.padding(vertical = 32.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                fontFamily = FontFamily(Font(R.font.poppins_bold))
            )
        } else {
            selectedExercises.mapIndexed { i, e ->
                ExerciseItem(
                    exercise = e,
                    exerciseViewModel = exerciseViewModel,
                    initialNote = exerciseViewModel.exerciseNotes[e] ?: "",
                )
            }
        }

        TextButton(
            onClick = {
                navController.navigate(Routes.WorkoutExercisesScreen.route)
            }
        ) {
            Text(
                "Add Exercise",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary.copy(0.8f),
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )
        }

        TextButton(
            onClick = {
                showCancelDialog = true
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                "Cancel Workout",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red.copy(0.8f),
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )
        }
    }
}