package com.commonsware.todo

import androidx.lifecycle.ViewModel

class RosterMotor(private val repository: ToDoRepository) : ViewModel() {
    val items = repository.items
}