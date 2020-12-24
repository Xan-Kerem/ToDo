package com.commonsware.todo

import androidx.lifecycle.ViewModel

class SingleModelMotor(private val repository: ToDoRepository, private val modelId: String) :
    ViewModel() {
    fun getModel() = repository.find(modelId)
    fun save(model: ToDoModel) {
        repository.save(model)
    }
}