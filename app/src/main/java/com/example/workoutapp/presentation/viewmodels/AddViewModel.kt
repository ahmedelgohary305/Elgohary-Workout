package com.example.workoutapp.presentation.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddViewModel @Inject constructor(): ViewModel() {
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private var job: Job? = null
    private var startTime: Long = 0L

    init {
        startTimer()
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        job = viewModelScope.launch {
            while (true) {
                _elapsedTime.value = (System.currentTimeMillis() - startTime) / 1000
                delay(1000L)
            }
        }
    }

    fun stopTimer(): Long {
        job?.cancel()
        return _elapsedTime.value
    }

    @SuppressLint("DefaultLocale")
    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", minutes, sec)
    }
}