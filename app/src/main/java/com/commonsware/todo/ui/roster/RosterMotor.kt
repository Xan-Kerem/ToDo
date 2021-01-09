package com.commonsware.todo.ui.roster

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.commonsware.todo.BuildConfig
import com.commonsware.todo.repo.FilterMode
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import com.commonsware.todo.report.RosterReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
data class RosterViewState(
    val items: List<ToDoModel> = listOf(),
    val filterMode: FilterMode = FilterMode.ALL
)

class RosterMotor(
    private val repository: ToDoRepository,
    private val report: RosterReport,
    private val context: Context,
    private val appScope: CoroutineScope
) :
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

    fun shareReport() {
        viewModelScope.launch {
            saveForSharing()
        }
    }

    private suspend fun saveForSharing() {
        withContext(Dispatchers.IO + appScope.coroutineContext){
            val shared = File(context.cacheDir, "shared").also{it.mkdirs()}
            val reportFile = File(shared, "report.html")
            val doc = FileProvider.getUriForFile(context, AUTHORITY,reportFile)

            _states.value?.let { report.generate(it.items, doc) }
            _navEvents.offer(Nav.ShareReport(doc))
        }
    }

    sealed class Nav {
        data class ViewReport(val doc: Uri) : Nav()
        data class ShareReport(val doc : Uri) : Nav()
    }

}