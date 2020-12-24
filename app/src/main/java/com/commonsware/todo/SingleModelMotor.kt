package com.commonsware.todo

import androidx.lifecycle.ViewModel

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