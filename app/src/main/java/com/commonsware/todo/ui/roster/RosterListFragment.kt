package com.commonsware.todo.ui.roster

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.commonsware.todo.R
import com.commonsware.todo.RosterAdapter
import com.commonsware.todo.databinding.TodoRosterBinding
import com.commonsware.todo.repo.FilterMode
import com.commonsware.todo.repo.ToDoModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQUEST_SAVE = 1337

class RosterListFragment : Fragment() {
    // viewModel by Koin
    private val motor: RosterMotor by viewModel()
    private lateinit var binding: TodoRosterBinding

    private val menuMap = mutableMapOf<FilterMode, MenuItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TodoRosterBinding.inflate(inflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            RosterAdapter(
                layoutInflater,
                onCheckBoxToggle = { model -> motor.save(model.copy(isCompleted = !model.isCompleted)) },
                onRowClick = ::displayModel
            )

        binding.items.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))


            motor.states.observe(viewLifecycleOwner) { state ->
                adapter.submitList(state.items)
                binding.loading.visibility = View.GONE
                when {
                    state.items.isEmpty() && state.filterMode == FilterMode.ALL -> {
                        binding.empty.visibility = View.VISIBLE
                        binding.empty.setText(R.string.msg_empty)
                    }
                    state.items.isEmpty() -> {
                        binding.empty.visibility = View.VISIBLE
                        binding.empty.setText(R.string.msg_empty_filtered)
                    }
                    else -> binding.empty.visibility = View.GONE
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            motor.navEvents.collect { nav ->
                when (nav) {
                    is RosterMotor.Nav.ViewReport -> viewReport(nav.doc)
                    is RosterMotor.Nav.ShareReport -> shareReport(nav.doc)
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SAVE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { motor.saveReport(it) }
            }
        }
    }

    private fun displayModel(model: ToDoModel) {
        findNavController().navigate(RosterListFragmentDirections.actionDisplayModel(model.id))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster, menu)

        menuMap.apply {
            put(FilterMode.ALL, menu.findItem(R.id.all))
            put(FilterMode.COMPLETED, menu.findItem(R.id.completed))
            put(FilterMode.OUTSTANDING, menu.findItem(R.id.outstanding))
        }

        motor.states.value?.let {
            menuMap[it.filterMode]?.isChecked = true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                add()
                return true
            }
            R.id.all -> {
                item.isChecked = true
                motor.load(FilterMode.ALL)
                return true
            }
            R.id.completed -> {
                item.isChecked = true
                motor.load(FilterMode.COMPLETED)
                return true
            }
            R.id.outstanding -> {
                item.isChecked = true
                motor.load(FilterMode.OUTSTANDING)
            }
            R.id.save -> {
                saveReport()
                return true
            }
            R.id.share -> {
                motor.shareReport()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun add() {
        findNavController().navigate(
            RosterListFragmentDirections.createModel()
        )
    }

    private fun safeStartActivity(intent: Intent, resultCode: Int? = null) {
        try {
            resultCode?.let { startActivityForResult(intent, it) } ?: startActivity(intent)
        } catch (t: Throwable) {
            Toast.makeText(requireContext(), R.string.oops, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveReport() {
        safeStartActivity(
            Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType("text/html"), REQUEST_SAVE
        )
    }

    private fun viewReport(uri: Uri) {
        safeStartActivity(
            Intent(
                Intent.ACTION_VIEW,
                uri
            ).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
    }

    private fun shareReport(doc: Uri) {
        safeStartActivity(Intent(Intent.ACTION_SEND).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            type = "text/html"
            putExtra(Intent.EXTRA_STREAM, doc)
        })
    }

}