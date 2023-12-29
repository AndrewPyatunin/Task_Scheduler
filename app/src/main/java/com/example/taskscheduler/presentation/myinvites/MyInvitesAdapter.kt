package com.example.taskscheduler.presentation.myinvites

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.DiffCallback
import com.example.taskscheduler.domain.models.Invite

class MyInvitesAdapter: RecyclerView.Adapter<MyInvitesAdapter.MyInvitesViewHolder>() {
    var onItemClick: ((Invite) -> Unit)? = null
    var invitesList: List<Invite> = emptyList()
        set(value) {
            val diffCallback = DiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            field = value
        }

    inner class MyInvitesViewHolder(
        itemView: View,
        val textViewInvite: TextView = itemView.findViewById(R.id.textViewInvite),
        val textViewInBoard: TextView = itemView.findViewById(R.id.textViewInBoard)
        ): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(invitesList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyInvitesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_invite_item, parent, false)
        return MyInvitesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyInvitesViewHolder, position: Int) {
        val item = invitesList[position]
        Log.i("USER_INVITE_ITEM", item.userName)
        val context = holder.itemView.context
        holder.textViewInvite.text = context.getString(R.string.invite_from, "${item.userName} ${item.userLastName}")
        holder.textViewInBoard.text = context.getString(R.string.to_board, item.boardName)
    }

    override fun getItemCount(): Int = invitesList.size
}