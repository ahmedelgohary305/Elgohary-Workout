package com.example.workoutapp.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workoutapp.R
import com.example.workoutapp.presentation.viewmodels.ExerciseViewModel
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun ExerciseItem(
    exercise: String,
    exerciseViewModel: ExerciseViewModel,
    initialNote: String,
) {
    val sets by exerciseViewModel.exerciseSets.collectAsStateWithLifecycle()
    val selectedExerciseSets by exerciseViewModel.selectedExerciseSets.collectAsStateWithLifecycle()
    val isAddingNote = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val note = rememberSaveable { mutableStateOf(initialNote) }
    val previousStats by exerciseViewModel.previousExerciseStats.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        exerciseViewModel.initializeExercise(exercise)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                color = MaterialTheme.colorScheme.primary
            )

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_more_vert_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (isAddingNote.value) {
                        DropdownMenuItem(
                            text = { Text("Remove Note") },
                            onClick = {
                                expanded = false
                                isAddingNote.value = false
                                note.value = ""
                                exerciseViewModel.updateExerciseNote(exercise, "")
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
                    DropdownMenuItem(
                        text = { Text("Delete Exercise") },
                        onClick = {
                            expanded = false
                            exerciseViewModel.deleteExercise(exercise)
                        }
                    )
                }
            }
        }

        if (isAddingNote.value) {
            TextField(
                value = note.value,
                onValueChange = {
                    note.value = it
                    exerciseViewModel.updateExerciseNote(exercise, it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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

        sets[exercise]?.forEachIndexed { index, set ->
            val previousSet = previousStats[exercise]?.getOrNull(index)
            val isSelected = selectedExerciseSets[exercise]?.any { it.id == set.id } == true

            ExerciseData(
                setNumber = index + 1,
                previous = previousSet?.let { "${it.kg}×${it.reps}×${it.rir}" } ?: "--",
                kg = previousSet?.kg ?: 0,
                reps = previousSet?.reps ?: 0,
                rir = previousSet?.rir ?: 0,
                onKgChange = { newKg ->
                    exerciseViewModel.updateSet(exercise, index, newKg, set.reps, set.rir)
                },
                onRepsChange = { newReps ->
                    exerciseViewModel.updateSet(exercise, index, set.kg, newReps, set.rir)
                },
                onRirChange = { newRir ->
                    exerciseViewModel.updateSet(exercise, index, set.kg, set.reps, newRir)
                },
                isSelected = isSelected,
                onClick = {
                    exerciseViewModel.toggleExerciseSetSelection(exercise, set)
                },
                exerciseViewModel = exerciseViewModel,
                set = set,
            )
        }



        TextButton(
            onClick = {
                exerciseViewModel.addSet(exercise)
            }
        ) {
            Text(
                "Add Set",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )
        }
    }
}


data class ExerciseSet(
    val id: String = UUID.randomUUID().toString(),
    val kg: Int,
    val reps: Int,
    val rir: Int
)

@Composable
fun ExerciseData(
    setNumber: Int,
    previous: String,
    kg: Int,
    reps: Int,
    rir: Int,
    onRirChange: (Int) -> Unit,
    onKgChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    exerciseViewModel: ExerciseViewModel,
    set: ExerciseSet,
) {
    val context = LocalContext.current
    val activeTimerSetId by exerciseViewModel.activeTimerSetId.collectAsStateWithLifecycle()
    val finishedTimers by exerciseViewModel.finishedTimers.collectAsStateWithLifecycle()
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
        else Color.Transparent,
        animationSpec = tween(durationMillis = 100),
        label = "Background Animation"
    )
    var timerText by rememberSaveable { mutableStateOf("02:00") }
    var isTimerFinished by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isTimerFinished) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        animationSpec = tween(durationMillis = 100),
        label = "Background Change"
    )

    LaunchedEffect(activeTimerSetId) {
        isTimerFinished = finishedTimers.contains(set.id)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onClick()
                    if (isTimerFinished) {
                        isTimerFinished = false
                    }
                }
                .background(if (isTimerFinished) Color.Transparent else animatedColor)
                .padding(8.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            )
            {
                ExerciseDataItem(
                    statName = "SET",
                    statValue = setNumber.toString(),
                    modifier = Modifier.weight(0.5f)
                )
                ExerciseDataItem(
                    statName = "PREVIOUS",
                    statValue = previous,
                    modifier = Modifier.weight(2f)
                )
                ExerciseDataItemWithInput(
                    statName = "KG",
                    statValue = kg,
                    onValueChange = onKgChange,
                    isSelected = isSelected,
                    modifier = Modifier.weight(1f)
                )
                ExerciseDataItemWithInput(
                    statName = "REPS",
                    statValue = reps,
                    onValueChange = onRepsChange,
                    isSelected = isSelected,
                    modifier = Modifier.weight(1f)
                )
                ExerciseDataItemWithInput(
                    statName = "RIR",
                    statValue = rir,
                    onValueChange = onRirChange,
                    isSelected = isSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (!isTimerFinished) {
            if (isSelected) {
                CountdownTimer(
                    initialTimeText = timerText,
                    onTimerFinished = { isTimerFinished = true }, context = context
                )
            } else {
                AdjustableTimer(onTimeSet = { timerText = it }, timerText)
            }
        } else {
            FinishedCountdownTime(timerText)
        }
    }
}

@Composable
fun FinishedCountdownTime(timerText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        FinishedDivider()
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            timerText,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = MaterialTheme.colorScheme.secondary.copy(0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(8.dp))
        FinishedDivider()
    }
}

@Composable
fun RowScope.FinishedDivider() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun CountdownTimer(initialTimeText: String, onTimerFinished: () -> Unit, context: Context) {
    val totalTime = remember(initialTimeText) {
        initialTimeText.split(":").mapNotNull { it.toIntOrNull() }
            .let { it.getOrNull(0)?.times(60)?.plus(it.getOrNull(1) ?: 0) ?: 120 }
            .toFloat()
    }

    var timeLeft by remember { mutableFloatStateOf(totalTime) }
    val progress by derivedStateOf { timeLeft / totalTime }

    val soundPool = remember { SoundPool.Builder().setMaxStreams(1).build() }
    val soundId = remember { soundPool.load(context, R.raw.boxing_bell, 1) }


    DisposableEffect(Unit) {
        onDispose {
            Handler(Looper.getMainLooper()).postDelayed({
                soundPool.release()
            }, 3000)
        }
    }

    LaunchedEffect(Unit) {
        val startTime = withFrameMillis { it }
        var hasPlayedSound = false

        while (timeLeft > 0) {
            timeLeft = (totalTime - (withFrameMillis { it } - startTime) / 1000f).coerceAtLeast(0f)

            if (timeLeft < 0.5f && !hasPlayedSound) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                hasPlayedSound = true
            }

            delay(16L)
        }

        onTimerFinished()
    }

    TimerUI(timeLeft, progress)
}


@SuppressLint("DefaultLocale")
@Composable
fun TimerUI(timeLeft: Float, progress: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .height(30.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(0.2f)
        )
        Text(
            String.format("%02d:%02d", (timeLeft / 60).toInt(), (timeLeft % 60).toInt()),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}


@Composable
fun AdjustableTimer(onTimeSet: (String) -> Unit, timerText: String) {
    var timeText by remember { mutableStateOf(timerText) }
    var isEditing by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val textFieldValue = remember { mutableStateOf(TextFieldValue(timeText)) }

    fun formatTime(input: String) = input.filter { it.isDigit() }
        .takeLast(4)
        .padStart(4, '0')
        .let { "${it.take(2)}:${it.takeLast(2)}" }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            delay(100)
            focusRequester.requestFocus()
            textFieldValue.value =
                textFieldValue.value.copy(selection = TextRange(0, timeText.length))
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 80.dp, height = 40.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { isEditing = true }
        ) {
            if (isEditing) {
                BasicTextField(
                    value = textFieldValue.value,
                    onValueChange = {
                        val formattedTime = formatTime(it.text)
                        textFieldValue.value =
                            TextFieldValue(formattedTime, TextRange(formattedTime.length))
                        timeText = formattedTime
                        onTimeSet(timeText)
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { isEditing = false }),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.9f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) textFieldValue.value = textFieldValue.value.copy(
                                selection = TextRange(
                                    0,
                                    timeText.length
                                )
                            )
                        }
                )
            } else {
                Text(
                    timerText,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = MaterialTheme.colorScheme.onSurface.copy(0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ExerciseDataItem(statName: String, statValue: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.height(80.dp)
    ) {
        Text(
            statName,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = MaterialTheme.colorScheme.onSurface.copy(0.9f)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 60.dp, max = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                statValue,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
            )
        }
    }
}

@Composable
fun ExerciseDataItemWithInput(
    statName: String,
    statValue: Int,
    onValueChange: (Int) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.height(80.dp)
    ) {
        Text(
            statName,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = MaterialTheme.colorScheme.onSurface.copy(0.9f)
        )
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            NumberPickerTextField(
                value = statValue,
                onValueChange = onValueChange,
                isSelected = isSelected
            )
        }
    }
}


@Composable
fun NumberPickerTextField(
    value: Int,
    onValueChange: (Int) -> Unit,
    isSelected: Boolean
) {
    var text by rememberSaveable { mutableStateOf(value.toString()) }

    TextField(
        value = text,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            val intValue = filteredValue.toIntOrNull()?.coerceIn(0, 999)

            text = if (newValue.isEmpty()) "" else intValue?.toString() ?: ""
            onValueChange(intValue ?: 0)

        },
        enabled = !isSelected,
        modifier = Modifier
            .width(61.dp)
            .height(48.dp),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontFamily = FontFamily(Font(R.font.poppins_regular))
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.inversePrimary.copy(0.9f),
            focusedContainerColor = MaterialTheme.colorScheme.inversePrimary,
            disabledContainerColor = MaterialTheme.colorScheme.inversePrimary
        ),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}


