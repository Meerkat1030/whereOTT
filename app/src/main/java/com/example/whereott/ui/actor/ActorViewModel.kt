package com.example.whereott.ui.actor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActorViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is actor Fragment"
    }
    val text: LiveData<String> = _text
}