package com.commonsware.todo.ui

import androidx.lifecycle.ViewModel
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository

class SingleModelMotor(private val repository: ToDoRepository, private val modelId: String?) :
    ViewModel() {
    fun getModel() = modelId?.let { repository.find(it) }
    fun save(model: ToDoModel) {
        repository.save(model)
    }
    fun delete(model: ToDoModel){
        repository.delete(model)
    }
}