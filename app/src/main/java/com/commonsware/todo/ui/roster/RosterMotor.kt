package com.commonsware.todo.ui.roster

import androidx.lifecycle.ViewModel
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository

class RosterMotor(private val repository: ToDoRepository) : ViewModel() {
    fun save(model: ToDoModel) {
        repository.save(model)
    }

    fun getItems() = repository.items
}