package com.commonsware.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.commonsware.todo.databinding.TodoRowBinding
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.ui.roster.RosterRowHolder

class RosterAdapter(
    private val layoutInflater: LayoutInflater,
    private val onCheckBoxToggle: (ToDoModel) -> Unit,
    private val onRowClick: (ToDoModel) -> Unit
) :
    ListAdapter<ToDoModel, RosterRowHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RosterRowHolder(
            TodoRowBinding.inflate(layoutInflater, parent, false),
            onCheckBoxToggle,
            onRowClick
        )

    override fun onBindViewHolder(holder: RosterRowHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private object DiffCallback : DiffUtil.ItemCallback<ToDoModel>() {
    override fun areItemsTheSame(oldItem: ToDoModel, newItem: ToDoModel) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ToDoModel, newItem: ToDoModel) =
        oldItem.isCompleted == newItem.isCompleted && oldItem.description == newItem.description
}