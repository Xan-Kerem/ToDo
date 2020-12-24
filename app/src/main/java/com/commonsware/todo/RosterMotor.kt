package com.commonsware.todo

import androidx.lifecycle.ViewModel

class RosterMotor(private val repository: ToDoRepository) : ViewModel() {
    fun save(model: ToDoModel) {
        repository.save(model)
    }

    fun getItems() = repository.items
}