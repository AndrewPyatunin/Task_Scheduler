package com.example.taskscheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.presentation.board.BoardFragment

class ParentListDiffCallback(
    private val oldList: List<ListOfNotesItem>,
    private val newList: List<ListOfNotesItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldListOfNotes = oldList[oldItemPosition]
        val newListOfNotes = newList[newItemPosition]
        return oldListOfNotes.title == newListOfNotes.title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldListOfNotes = oldList[oldItemPosition]
        val newListOfNotes = newList[newItemPosition]
        return oldListOfNotes == newListOfNotes
    }

}
class ParentListNoteRVAdapter(var parentListFrom: List<ListOfNotesItem>, val clickHandler: BoardFragment.MyNoteClickHandler): RecyclerView.Adapter<ParentListNoteRVAdapter.ParentViewHolder>() {
    var onItemClick: ((ListOfNotesItem) -> Unit)? = null
    var parentList = parentListFrom
    set(newValue) {
        val diffCallback = ParentListDiffCallback(field, newValue)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        field = newValue
    }
    inner class ParentViewHolder(
        itemView: View,
        clickHandler: BoardFragment.MyNoteClickHandler,
        val textViewAddNote: TextView = itemView.findViewById(R.id.textViewAddCard),
        val textViewListTitle: TextView = itemView.findViewById(R.id.list_of_notes_title),
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.child_recyclerViewBoard)
    ): RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(parentList[adapterPosition])
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parent_recyclerview_item, parent, false)

        return ParentViewHolder(view, clickHandler)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {

        val parentItem = parentList[position]

        holder.textViewListTitle.text = parentItem.title
//        holder.childRecyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 1, GridLayoutManager.HORIZONTAL, false)
        holder.textViewAddNote.setOnClickListener {
            clickHandler.onTextClicked(R.id.textViewAddCard, position)
        }
        val childAdapter = ChildNoteRVAdapter(parentItem.listNotes.values.toList(), clickHandler)
//        childAdapter.onItemClick = {
//            Toast.makeText(holder.itemView.context, it.title, Toast.LENGTH_SHORT).show()
//        }
//        for (i in parentItem.listNotes) {
//            childAdapter.listOfNotes.add(i)
//        }
        holder.childRecyclerView.adapter = childAdapter
    }

    override fun getItemCount(): Int = parentList.size

    interface ExternalClickDelegate {
        fun onItemClickedAt(position: Int)
    }

}