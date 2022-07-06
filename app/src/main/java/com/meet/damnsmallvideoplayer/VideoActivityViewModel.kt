package com.meet.damnsmallvideoplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoActivityViewModel : ViewModel() {
    var currentPosition = MutableLiveData<Int>(0)
    fun updatePosition(pos: Int) {
        currentPosition.value = pos
    }
}