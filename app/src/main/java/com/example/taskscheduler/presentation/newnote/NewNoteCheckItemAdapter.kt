package com.example.taskscheduler.presentation.newnote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.DiffCallback


class NewNoteCheckItemAdapter(checkItems: List<CheckNoteItem> = ArrayList()):
    RecyclerView.Adapter<NewNoteCheckItemAdapter.NewNoteCheckItemViewHolder>() {

    var onItemClick: ((CheckNoteItem) -> Unit)? = null
    var checkItemsList = emptyList<CheckNoteItem>()
        set(value) {
            val diffCallback = DiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            field = value
        }

    inner class NewNoteCheckItemViewHolder(
        itemView: View,
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxNoteItem),
        val itemTitleText: TextView = itemView.findViewById(R.id.textViewNoteItemTitle),
        val checkStateSave: ImageView = itemView.findViewById(R.id.saveStateCheck)
        ): RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    onItemClick?.invoke(checkItemsList[adapterPosition])
                    checkBox.isChecked = !checkBox.isChecked
//                    checkStateSave.visibility = View.VISIBLE
//                    itemView.setOnClickListener {
//                        onImageClick?.invoke(checkStateSave)
//                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewNoteCheckItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_check_list_item, parent, false)
        return NewNoteCheckItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewNoteCheckItemViewHolder, position: Int) {
        holder.itemTitleText.text = checkItemsList[position].itemTitle
        holder.checkBox.isChecked = checkItemsList[position].isChecked
        holder.checkBox.setOnClickListener {
            onItemClick?.invoke(checkItemsList[position])
        }
    }

    override fun getItemCount(): Int = checkItemsList.size
}