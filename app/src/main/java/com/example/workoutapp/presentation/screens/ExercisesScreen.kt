package com.example.workoutapp.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.workoutapp.R
import com.example.workoutapp.data.local.CreatedExerciseEntity
import com.example.workoutapp.presentation.viewmodels.ExerciseViewModel
import com.example.workoutapp.presentation.viewmodels.WorkoutViewModel


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ExercisesScreen(
    exerciseViewModel: ExerciseViewModel,
    workoutViewModel: WorkoutViewModel,
    navController: NavController,
    context: Context
) {
    val allExercises = workoutViewModel.createdExercisesList.collectAsStateWithLifecycle().value
    val selectedExercises = exerciseViewModel.selectedExercises.collectAsStateWithLifecycle()

    var showOptionsMenu by rememberSaveable { mutableStateOf(false) }
    var showCreateExerciseDialog by rememberSaveable { mutableStateOf(false) }
    var showBodyPartDialog by rememberSaveable { mutableStateOf(false) }
    var showFilterDialog by rememberSaveable { mutableStateOf(false) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilterBodyParts by rememberSaveable { mutableStateOf(listOf<String>()) }
    var isSearching by rememberSaveable { mutableStateOf(false) }

    val filteredExercises = remember(searchQuery, selectedFilterBodyParts, allExercises) {
        allExercises.filter {
            (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)) &&
                    (selectedFilterBodyParts.isEmpty() || it.bodyPart in selectedFilterBodyParts)
        }
    }

    val groupedExercises = filteredExercises.map { it.name }.groupBy { it.first().uppercaseChar() }
    val bodyParts = allExercises.map { it.bodyPart }.toSet().toList()

    var exerciseName by rememberSaveable { mutableStateOf("") }
    var selectedBodyPart by rememberSaveable { mutableStateOf("") }

    BackHandler(enabled = selectedExercises.value.isNotEmpty()) {
        exerciseViewModel.clearSelectedExercisesInExerciseScreen()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isSearching) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(
                            0.3f
                        ),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.3f)
                    ),
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = 16.sp
                    ),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                isSearching = false
                            },
                            painter = painterResource(R.drawable.baseline_close_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions {
                        isSearching = false
                    },
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            "Name",
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                        )
                    }
                )
            } else {
                Text(
                    text = "Select Exercises",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            isSearching = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_search_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    IconButton(
                        onClick = {
                            showFilterDialog = true
                        }
                    ) {
                        if (selectedFilterBodyParts.isNotEmpty()) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_filter_alt_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.outline_filter_alt_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = {
                                showOptionsMenu = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_more_vert_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )

                        }
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Create Exercise") },
                                onClick = {
                                    showOptionsMenu = false
                                    showCreateExerciseDialog = true
                                },
                            )
                        }
                    }
                }
            }
        }
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showFilterDialog = false
                        }
                    ) {
                        Text(
                            "OK",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                },
                dismissButton = {},
                title = {
                    Text(
                        "Filter Exercises",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                },
                text = {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        bodyParts.map {
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                                    .clickable {
                                        selectedFilterBodyParts =
                                            if (it in selectedFilterBodyParts) {
                                                selectedFilterBodyParts - it
                                            } else {
                                                selectedFilterBodyParts + it
                                            }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (it in selectedFilterBodyParts)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                ),
                            ) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(8.dp),
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            )
        }

        if (showCreateExerciseDialog) {
            AlertDialog(
                onDismissRequest = { showCreateExerciseDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (exerciseName !in allExercises.map { it.name }){
                            if (exerciseName.isNotEmpty() || selectedBodyPart.isNotEmpty()) {
                                workoutViewModel.insertCreatedExercise(
                                    CreatedExerciseEntity(
                                        name = exerciseName,
                                        bodyPart = selectedBodyPart
                                    )
                                )
                            }
                            exerciseViewModel.addToSelectedExercises(exerciseName)
                            exerciseName = ""
                            selectedBodyPart = ""
                            showCreateExerciseDialog = false
                        }else{
                            Toast.makeText(context, "There is exercise with same name", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ) {
                        Text(
                            "OK",
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        exerciseName = ""
                        selectedBodyPart = ""
                        showCreateExerciseDialog = false
                    }) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    }
                },
                title = {
                    Text(
                        "Create Exercise",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                },
                text = {
                    Column {
                        TextField(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(
                                    0.3f
                                ),
                                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.3f)
                            ),
                            textStyle = TextStyle(
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontSize = 16.sp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = {
                                Text(
                                    "Name",
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(0.3f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showBodyPartDialog = true }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (selectedBodyPart.isNotEmpty()) selectedBodyPart else "Select Body Part",
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (showBodyPartDialog) {
                            AlertDialog(
                                onDismissRequest = { showBodyPartDialog = false },
                                title = {
                                    Text(
                                        "Select Body Part",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                                    )
                                },
                                text = {
                                    LazyColumn {
                                        items(bodyParts) { part ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedBodyPart = part
                                                        showBodyPartDialog = false
                                                    },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = selectedBodyPart == part,
                                                    onClick = {
                                                        selectedBodyPart = part
                                                        showBodyPartDialog = false
                                                    }
                                                )
                                                Text(
                                                    text = part,
                                                    modifier = Modifier.padding(start = 8.dp),
                                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                                    fontSize = 16.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                },
                                confirmButton = {}
                            )
                        }
                    }
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                groupedExercises.forEach { (letter, exercises) ->
                    item {
                        Text(
                            text = letter.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary.copy(0.8f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    items(exercises) { exercise ->
                        val isSelected = exercise in selectedExercises.value

                        val barScale = animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0f,
                            animationSpec = tween(durationMillis = 300)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isSelected) {
                                        exerciseViewModel.removeFromSelectedExercises(exercise)
                                    } else {
                                        exerciseViewModel.addToSelectedExercises(exercise)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(6.dp * barScale.value)
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                )

                                Text(
                                    text = exercise,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Text(
                                text = allExercises.find { it.name == exercise }?.bodyPart
                                    ?: "",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                modifier = Modifier.padding(start = 16.dp)
                            )

                        }
                    }
                }
            }

            val scale = animateFloatAsState(
                targetValue = if (selectedExercises.value.isNotEmpty()) 1f else 0f,
                animationSpec = tween(durationMillis = 300)
            )

            val alpha = animateFloatAsState(
                targetValue = if (selectedExercises.value.isNotEmpty()) 1f else 0f,
                animationSpec = tween(durationMillis = 300)
            )

            if (selectedExercises.value.isNotEmpty() || scale.value > 0f) {
                FloatingActionButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .padding(32.dp)
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = alpha.value
                        )
                        .align(Alignment.BottomEnd),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirm Selection")
                }
            }
        }
    }
}
