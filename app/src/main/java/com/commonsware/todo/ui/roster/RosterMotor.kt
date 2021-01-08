package com.commonsware.todo.ui.roster

import android.net.Uri
import androidx.lifecycle.*
import com.commonsware.todo.repo.FilterMode
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import com.commonsware.todo.report.RosterReport
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val filterMode: FilterMode = FilterMode.ALL
)

class RosterMotor(private val repository: ToDoRepository, private val report: RosterReport) :
    ViewModel() {
    private val _states = MediatorLiveData<RosterViewState>()
    private val _navEvents = BroadcastChannel<Nav>(Channel.BUFFERED)
    val navEvents = _navEvents.asFlow()
    val states: LiveData<RosterViewState> = _states

    private var lastSource: LiveData<RosterViewState>? = null

    init {
        load(FilterMode.ALL)
    }

    fun load(filterMode: FilterMode) {
        lastSource?.let {
            _states.removeSource(it)
        }
        val items =
            repository.items(filterMode).map { RosterViewState(it, filterMode) }.asLiveData()
        _states.addSource(items) { viewState ->
            _states.value = viewState
        }
        lastSource = items
    }

    fun save(model: ToDoModel) {
        viewModelScope.launch {
            repository.save(model)
        }
    }

    fun saveReport(doc: Uri) {
        viewModelScope.launch {
            _states.value?.let {
                report.generate(it.items, doc)
            }
            _navEvents.offer(Nav.ViewReport(doc))
        }
    }

    sealed class Nav {
        data class ViewReport(val doc: Uri) : Nav()
    }

}