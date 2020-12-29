package com.commonsware.todo.ui.roster

import androidx.lifecycle.*
import com.commonsware.todo.repo.FilterMode
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val filterMode: FilterMode = FilterMode.ALL
)

class RosterMotor(private val repository: ToDoRepository) : ViewModel() {
    private val _states = MediatorLiveData<RosterViewState>()

    val states: LiveData<RosterViewState> = _states

    private var lastSource : LiveData<RosterViewState>? = null

    init {
        load(FilterMode.ALL)
    }

    fun load(filterMode: FilterMode){
        lastSource?.let {
            _states.removeSource(it)
        }
        val items = repository.items(filterMode).map { RosterViewState(it, filterMode) }.asLiveData()
        _states.addSource(items){viewState ->
            _states.value = viewState
        }
        lastSource = items
    }
    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repository.save(model)
        }
    }


}