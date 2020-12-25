package com.commonsware.todo.ui.roster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class RosterViewState(val items: List<ToDoModel> = listOf())
class RosterMotor(private val repository: ToDoRepository) : ViewModel() {
    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repository.save(model)
        }
    }

    val states = repository.items().map { RosterViewState(it) }.asLiveData()


}