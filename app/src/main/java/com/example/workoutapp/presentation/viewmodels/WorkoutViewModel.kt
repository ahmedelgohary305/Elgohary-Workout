package com.example.workoutapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.local.CreatedExerciseEntity
import com.example.workoutapp.data.local.ExerciseEntity
import com.example.workoutapp.data.local.ExerciseWithSets
import com.example.workoutapp.data.local.SetEntity
import com.example.workoutapp.data.local.WorkoutEntity
import com.example.workoutapp.data.local.WorkoutWithExercisesAndSets
import com.example.workoutapp.domain.WorkoutRepo
import com.example.workoutapp.presentation.utils.ExerciseSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

val exerciseBodyParts = mapOf(
    "Ab Rollout" to "Core",
    "Ab Wheel Rollout" to "Core",
    "Arnold Press" to "Shoulders",
    "Atlas Stones" to "Full Body",
    "Back Extension" to "Lower Back",
    "Back Squat" to "Legs",
    "Ball Slams" to "Full Body",
    "Barbell Curl" to "Biceps",
    "Barbell Row" to "Back",
    "Barbell Shrug" to "Traps",
    "Battle Ropes" to "Full Body",
    "Bench Dips" to "Triceps",
    "Bench Press" to "Chest",
    "Bent-over Row" to "Back",
    "Bicycle Crunches" to "Core",
    "Bicep Curl" to "Biceps",
    "Bodyweight Squat" to "Legs",
    "Box Jump" to "Legs",
    "Broad Jump" to "Legs",
    "Bulgarian Split Squat" to "Legs",
    "Burpees" to "Full Body",
    "Cable Crossover" to "Chest",
    "Cable Fly" to "Chest",
    "Cable Lateral Raise" to "Shoulders",
    "Cable Row" to "Back",
    "Calf Raises" to "Calves",
    "Cannonball Squat" to "Legs",
    "Chest Dips" to "Chest",
    "Chest Fly" to "Chest",
    "Chin-ups" to "Back",
    "Clean and Jerk" to "Full Body",
    "Clean and Press" to "Full Body",
    "Close-Grip Bench Press" to "Triceps",
    "Close-Grip Pull-ups" to "Back",
    "Cluster Set Squats" to "Legs",
    "Copenhagen Plank" to "Core",
    "Concentration Curl" to "Biceps",
    "Core Twists" to "Core",
    "Crunches" to "Core",
    "Cyclist Squat" to "Legs",
    "Dead Hang" to "Grip Strength",
    "Deadlift" to "Full Body",
    "Decline Bench Press" to "Chest",
    "Deficit Deadlift" to "Hamstrings",
    "Deficit Push-ups" to "Chest",
    "Diamond Push-ups" to "Triceps",
    "Dips" to "Triceps",
    "Donkey Kicks" to "Glutes",
    "Dumbbell Arnold Press" to "Shoulders",
    "Dumbbell Bench Press" to "Chest",
    "Dumbbell Bulgarian Split Squat" to "Legs",
    "Dumbbell Chest Fly" to "Chest",
    "Dumbbell Deadlift" to "Legs",
    "Dumbbell Fly" to "Chest",
    "Dumbbell Hammer Curl" to "Biceps",
    "Dumbbell Lateral Raise" to "Shoulders",
    "Dumbbell Lunges" to "Legs",
    "Dumbbell Overhead Press" to "Shoulders",
    "Dumbbell Pullover" to "Chest",
    "Dumbbell Row" to "Back",
    "Dumbbell Snatch" to "Full Body",
    "Dumbbell Step-ups" to "Legs",
    "Dumbbell Squat" to "Legs",
    "Dumbbell Thrusters" to "Full Body",
    "Dumbbell Upright Row" to "Shoulders",
    "Dynamic Lunges" to "Legs",
    "Elevated Split Squat" to "Legs",
    "EZ-Bar Curl" to "Biceps",
    "Face Pulls" to "Shoulders",
    "Farmer’s Carry" to "Grip Strength",
    "Flat Bench Press" to "Chest",
    "Flutter Kicks" to "Core",
    "Front Lever" to "Core",
    "Front Squat" to "Legs",
    "Goblet Squat" to "Legs",
    "Glute Bridge" to "Glutes",
    "Good Mornings" to "Hamstrings",
    "Hack Squat" to "Legs",
    "Hammer Curl" to "Biceps",
    "Hanging Knee Raise" to "Core",
    "Handstand Push-ups" to "Shoulders",
    "Incline Bench Press" to "Chest",
    "Incline Dumbbell Fly" to "Chest",
    "Inverted Row" to "Back",
    "Jump Rope" to "Cardio",
    "Jump Squat" to "Legs",
    "Kettlebell Swing" to "Full Body",
    "Leg Curl" to "Hamstrings",
    "Leg Extension" to "Quads",
    "Leg Press" to "Legs",
    "Lunges" to "Legs",
    "Medicine Ball Slam" to "Core",
    "Military Press" to "Shoulders",
    "Mountain Climbers" to "Core",
    "Nordic Hamstring Curl" to "Hamstrings",
    "Overhead Press" to "Shoulders",
    "Overhead Squat" to "Legs",
    "Plank" to "Core",
    "Pull-ups" to "Back",
    "Push Press" to "Shoulders",
    "Push-ups" to "Chest",
    "Renegade Rows" to "Back",
    "Reverse Fly" to "Shoulders",
    "Reverse Lunge" to "Legs",
    "Romanian Deadlift" to "Hamstrings",
    "Russian Twist" to "Obliques",
    "Seated Row" to "Back",
    "Shoulder Press" to "Shoulders",
    "Side Plank" to "Core",
    "Squats" to "Legs",
    "Step-ups" to "Legs",
    "Sumo Deadlift" to "Hamstrings",
    "Toe Touches" to "Core",
    "Triceps Dips" to "Triceps",
    "Triceps Extension" to "Triceps",
    "Turkish Get-up" to "Full Body",
    "Upright Row" to "Shoulders",
    "V-ups" to "Core",
    "Wall Sit" to "Legs",
    "Wide-Grip Pull-up" to "Back",
    "Zercher Squat" to "Legs"
)


@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepo
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<WorkoutWithExercisesAndSets>?>(null)
    val workouts: StateFlow<List<WorkoutWithExercisesAndSets>?> = _workouts

    private val _workoutName = MutableStateFlow("Workout 1")
    val workoutName: StateFlow<String> = _workoutName

    private val _createdExercisesList = MutableStateFlow<List<CreatedExerciseEntity>>(emptyList())
    val createdExercisesList: StateFlow<List<CreatedExerciseEntity>> = _createdExercisesList


    init {
        viewModelScope.launch {
            repository.getAllWorkouts().collect { workoutsList ->
                _workouts.value = workoutsList

                val maxWorkoutNumber = workoutsList
                    .mapNotNull { workout ->
                        workout.workout.name.substringAfter("Workout ", "")
                            .toIntOrNull()
                    }
                    .maxOrNull() ?: 0

                _workoutName.value = "Workout ${maxWorkoutNumber + 1}"
            }
        }

        insertCreatedExercises(exerciseBodyParts.map {
            CreatedExerciseEntity(
                name = it.key,
                bodyPart = it.value
            )
        })

        getAllCreatedExercises()
    }

    fun setWorkoutName(name: String) {
        _workoutName.value = name
    }

    fun insertWorkout(
        workoutName: String,
        note: String,
        time: String,
        exercises: Map<String, List<ExerciseSet>>,
        exerciseNotes: Map<String, String>
    ) {
        viewModelScope.launch {
            val workout = WorkoutEntity(name = workoutName, time = time, note = note)
            val exercisesWithSets = exercises.map { (exerciseName, sets) ->
                ExerciseWithSets(
                    exercise = ExerciseEntity(
                        name = exerciseName,
                        workoutId = 0,
                        note = exerciseNotes[exerciseName] ?: ""
                    ),
                    sets = sets.map {
                        SetEntity(
                            setId = 0,
                            exerciseId = 0,
                            kg = it.kg,
                            reps = it.reps,
                            rir = it.rir
                        )
                    }
                )
            }
            repository.insertWorkoutWithExercises(workout, exercisesWithSets)
        }
    }


    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun insertCreatedExercise(createdExercise: CreatedExerciseEntity) {
        viewModelScope.launch {
            repository.upsertCreatedExercise(createdExercise)
        }
    }

    fun insertCreatedExercises(createdExercises: List<CreatedExerciseEntity>) {
        viewModelScope.launch {
            repository.upsertCreatedExercises(createdExercises)
        }
    }

    fun getAllCreatedExercises() {
        viewModelScope.launch {
            repository.getAllCreatedExercises().collect { exercises ->
                _createdExercisesList.value = exercises
            }
        }
    }

    fun filterCreatedExercises(bodyParts: List<String>): List<CreatedExerciseEntity> {
        return if (bodyParts.isNotEmpty()){
            _createdExercisesList.value.filter {
                it.bodyPart in bodyParts
            }
        }else{
            _createdExercisesList.value
        }
    }

    fun searchCreatedExercises(query: String): List<CreatedExerciseEntity> {
        return _createdExercisesList.value.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}