package com.example.taskscheduler.presentation.boardupdated

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.Colors
import com.example.taskscheduler.domain.DiffCallback
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.UrgencyOfNote

class InnerBoardAdapter(listOfNotesFrom: List<Note>): RecyclerView.Adapter<InnerBoardAdapter.NoteViewHolder>()
{
    var listOfNotes = listOfNotesFrom
        set(newValue) {
            val diffCallback = DiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            field = newValue
        }
    var onItemClick: ((Note) -> Unit)? = null

    inner class NoteViewHolder(
        itemView: View,
        val textViewLabel: TextView = itemView.findViewById(R.id.label_note),
        val textViewTitle: TextView = itemView.findViewById(R.id.note_title),
        val viewPriority: TextView = itemView.findViewById(R.id.view_priority)
    ) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(listOfNotes[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_recyclerview_item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = listOfNotes[position]
        holder.textViewLabel.text = currentItem.date
        holder.textViewTitle.text = currentItem.title
        val color = when (currentItem.priority) {
            UrgencyOfNote.LOW -> Colors.GREEN
            UrgencyOfNote.MIDDLE -> Colors.YELLOW
            UrgencyOfNote.HIGH -> Colors.RED
        }
        holder.viewPriority.background = ResourcesCompat.getDrawable(holder.itemView.resources, color.res, null)
    }

    override fun getItemCount(): Int = listOfNotes.size

    interface InternalClickDelegate {
        fun onItemClickedAt(position: Int)
    }


}
