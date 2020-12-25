package com.commonsware.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class SingleModelViewState(val item: ToDoModel? = null)

class SingleModelMotor(private val repository: ToDoRepository, private val modelId: String?) :
    ViewModel() {

    val states = repository.find(modelId).map { SingleModelViewState(it) }.asLiveData()


    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repository.save(model)
        }
    }

    fun delete(model: ToDoModel) {
        viewModelScope.launch {
            repository.delete(model)
        }
    }
}