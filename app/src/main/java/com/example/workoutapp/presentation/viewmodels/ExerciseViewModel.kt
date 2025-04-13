package com.example.workoutapp.presentation.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.domain.WorkoutRepo
import com.example.workoutapp.presentation.utils.ExerciseSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: WorkoutRepo
) : ViewModel() {

    private val _activeTimerSetId = MutableStateFlow<String?>(null)
    val activeTimerSetId: StateFlow<String?> = _activeTimerSetId

    private val _finishedTimers = MutableStateFlow<Set<String>>(emptySet())
    val finishedTimers: StateFlow<Set<String>> = _finishedTimers

    private val _selectedExerciseSets = MutableStateFlow(mutableMapOf<String, List<ExerciseSet>>())
    val selectedExerciseSets: StateFlow<Map<String, List<ExerciseSet>>> = _selectedExerciseSets

    private val _exerciseSets = MutableStateFlow(mutableMapOf<String, List<ExerciseSet>>())
    val exerciseSets: StateFlow<Map<String, List<ExerciseSet>>> = _exerciseSets

    private val _previousExerciseStats = MutableStateFlow<Map<String, List<ExerciseSet>>>(emptyMap())
    val previousExerciseStats: StateFlow<Map<String, List<ExerciseSet>>> = _previousExerciseStats

    private val _selectedExercises = MutableStateFlow(listOf<String>())
    val selectedExercises: StateFlow<List<String>> = _selectedExercises

    private val _exerciseNotes = mutableStateMapOf<String, String>()
    val exerciseNotes: Map<String, String> get() = _exerciseNotes

    fun startWorkout(exercisesNames: List<String>, sets: List<List<ExerciseSet>>){
        _exerciseSets.value = exercisesNames.zip(sets).toMap().toMutableMap()
        _selectedExercises.value = exercisesNames
    }

    fun compareExerciseSetsAndSelected(): Boolean{
        val allExercises = _exerciseSets.value
        val selectedSets = _selectedExerciseSets.value

        return allExercises.all { (exerciseName, sets) ->
            selectedSets[exerciseName]?.containsAll(sets) == true
        }
    }

    fun toggleExerciseSetSelection(exerciseName: String, exerciseSet: ExerciseSet) {
        val currentMap = _selectedExerciseSets.value
        val currentList = currentMap[exerciseName].orEmpty()

        val updatedList = if (exerciseSet in currentList) {
            currentList - exerciseSet
        } else {
            currentList + exerciseSet
        }

        _selectedExerciseSets.value = currentMap.toMutableMap().apply {
            if (updatedList.isEmpty()) {
                remove(exerciseName)
            } else {
                put(exerciseName, updatedList)
            }
        }

        handleTimerLogic(exerciseSet.id,exerciseName)
    }

    private fun handleTimerLogic(setId: String, parentExerciseName: String) {
        val currentActiveId = _activeTimerSetId.value
        val selectedSetIds = _selectedExerciseSets.value.flatMap { it.value }.mapTo(mutableSetOf()) { it.id }

        when {
            setId in _finishedTimers.value -> {
                _finishedTimers.value -= setId
                _selectedExerciseSets.value = _selectedExerciseSets.value.toMutableMap().apply {
                    this[parentExerciseName] = this[parentExerciseName]?.filterNot { it.id == setId }.orEmpty()
                }

                if (currentActiveId == setId) _activeTimerSetId.value = null
            }

            currentActiveId == setId && setId !in selectedSetIds -> {
                _activeTimerSetId.value = null
            }

            currentActiveId != null && currentActiveId != setId -> {
                if (currentActiveId in selectedSetIds) {
                    _finishedTimers.value += currentActiveId
                }
                _activeTimerSetId.value = setId.takeIf { it in selectedSetIds }
            }

            else -> _activeTimerSetId.value = setId.takeIf { it in selectedSetIds }
        }
    }

    fun updateExerciseNote(exercise: String, note: String) {
        _exerciseNotes[exercise] = note
    }

    fun initializeExercise(exerciseName: String) {
        viewModelScope.launch {
            repository.getAllExerciseStats(exerciseName).collect { allStats ->
                _previousExerciseStats.value = _previousExerciseStats.value.toMutableMap().apply {
                    put(exerciseName, allStats)
                }
            }
        }

        if (!_exerciseSets.value.containsKey(exerciseName)) {
            _exerciseSets.value = _exerciseSets.value.toMutableMap().apply {
                if (_previousExerciseStats.value[exerciseName].isNullOrEmpty()){
                    put(exerciseName, listOf(ExerciseSet(kg = 0, reps = 0, rir = 0)))
                }else{
                    _previousExerciseStats.value[exerciseName]?.map {
                        ExerciseSet(kg = it.kg, reps = it.reps, rir = it.rir)
                    }?.let {
                        put(exerciseName, it)
                    }
                }
            }
        }
    }

    fun updateSet(exerciseName: String, index: Int, kg: Int, reps: Int, rir: Int) {
        _exerciseSets.value = _exerciseSets.value.toMutableMap().apply {
            val updatedList = get(exerciseName)?.toMutableList()?.apply {
                this[index] = this[index].copy(kg = kg, reps = reps, rir = rir)
            } ?: listOf()
            put(exerciseName, updatedList)
        }
    }

    fun addSet(exerciseName: String) {
        _exerciseSets.value = _exerciseSets.value.toMutableMap().apply {
            val updatedList = get(exerciseName)?.plus(ExerciseSet(kg = 0, reps = 0, rir = 0)) ?: listOf()
            put(exerciseName, updatedList)
        }
    }

    fun clearSelectedExercises() {
        _selectedExerciseSets.value = mutableMapOf()
        _selectedExercises.value = listOf()
        _exerciseSets.value = mutableMapOf()
        _exerciseNotes.clear()
    }

    fun clearSelectedExercisesInExerciseScreen(){
        _selectedExercises.value = listOf()
    }

    fun addToSelectedExercises(exerciseName: String) {
        _selectedExercises.value = _selectedExercises.value + exerciseName
    }

    fun removeFromSelectedExercises(exerciseName: String) {
        _selectedExercises.value = _selectedExercises.value - exerciseName
    }

    fun deleteExercise(exerciseName: String){
        _selectedExercises.value = _selectedExercises.value - exerciseName
        _exerciseSets.value = _exerciseSets.value.toMutableMap().apply {
            remove(exerciseName)
        }
        _exerciseNotes.remove(exerciseName)
        _selectedExerciseSets.value = _selectedExerciseSets.value.toMutableMap().apply {
            remove(exerciseName)
        }
    }
}