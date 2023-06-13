package com.example.taskscheduler

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView.Adapter
//import androidx.recyclerview.widget.RecyclerView.ViewHolder
//import com.example.taskscheduler.domain.ListOfNotesItem
//import com.example.taskscheduler.domain.Note
//import com.example.taskscheduler.presentation.board.BoardFragment
//
//class ChildNoteDiffCallback(
//    private val oldList: List<Note>,
//    private val newList: List<Note>
//) : DiffUtil.Callback() {
//    override fun getOldListSize(): Int = oldList.size
//
//    override fun getNewListSize(): Int = newList.size
//
//    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldNote = oldList[oldItemPosition]
//        val newNote = newList[newItemPosition]
//        return oldNote.id == newNote.id
//    }
//
//    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldNote = oldList[oldItemPosition]
//        val newNote = newList[newItemPosition]
//        return oldNote == newNote
//    }
//
//}
//
//class ChildNoteRVAdapter(listOfNotesFrom: ListOfNotesItem, val clickHandler: BoardFragment.MyNoteClickHandler): Adapter<ChildNoteRVAdapter.NoteViewHolder>()
//    {
//    private val internalClickHandler: InternalClickDelegate = object : InternalClickDelegate {
//        override fun onItemClickedAt(position: Int) {
//            clickHandler.run {
//                onNoteClicked(listOfNotes[position], listOfNotesFrom)
//            }
//        }
//
//    }
//    var listOfNotes = listOfNotesFrom.listNotes.values.toList()
//        set(newValue) {
//            val diffCallback = ChildNoteDiffCallback(field, newValue)
//            val diffResult = DiffUtil.calculateDiff(diffCallback)
//            diffResult.dispatchUpdatesTo(this)
//            field = newValue
//        }
//    var onItemClick: ((Note) -> Unit)? = null
//
//    inner class NoteViewHolder(
//        itemView: View,
//        internalClickHandler: InternalClickDelegate,
//        val textViewLabel: TextView = itemView.findViewById(R.id.label_note),
//        val textViewTitle: TextView = itemView.findViewById(R.id.note_title)
//    ) : ViewHolder(itemView), View.OnClickListener {
//        init {
//            itemView.setOnClickListener {
//                onItemClick?.invoke(listOfNotes[adapterPosition])
//            }
//        }
//
//        override fun onClick(v: View?) {
//            internalClickHandler.onItemClickedAt(adapterPosition)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_recyclerview_item_note, parent, false)
//        return NoteViewHolder(view, internalClickHandler)
//    }
//
//    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        val currentItem = listOfNotes[position]
//        holder.textViewLabel.text = currentItem.labels
//        holder.textViewTitle.text = currentItem.title
//        holder.itemView.setOnClickListener {
//            internalClickHandler.onItemClickedAt(position)
//        }
//    }
//
//    override fun getItemCount(): Int = listOfNotes.size
//
//    interface InternalClickDelegate {
//        fun onItemClickedAt(position: Int)
//    }
//
//
//}