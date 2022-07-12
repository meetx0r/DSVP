package com.meet.damnsmallvideoplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoActivityViewModel : ViewModel() {
    private var currentPosition = MutableLiveData(0)
    fun updatePosition(pos: Int) {
        currentPosition.value = pos
    }
}