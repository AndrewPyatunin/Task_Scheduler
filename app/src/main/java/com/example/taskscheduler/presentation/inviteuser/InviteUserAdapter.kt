package com.example.taskscheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.taskscheduler.domain.DiffCallback
import com.example.taskscheduler.domain.models.User

class UserListDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }

}

class InviteUserAdapter() : Adapter<InviteUserAdapter.InviteUserViewHolder>() {
    var onItemClick: ((User) -> Unit)? = null
    var users = ArrayList<User>()
    set(newValue) {
        val diffCallback = DiffCallback(field, newValue)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        field = newValue
    }

    inner class InviteUserViewHolder
        (itemView: View,
         var textViewUserInfo: TextView = itemView.findViewById(R.id.textViewUserInfo),
         var checkBoxInvited: CheckBox = itemView.findViewById(R.id.checkBoxInvited),
         var imageViewUserAvatar: ImageView = itemView.findViewById(R.id.imageViewUserAvatar),
         var viewStatus: View = itemView.findViewById(R.id.circle_status)
    ): ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(users[adapterPosition])
                checkBoxInvited.isChecked = !checkBoxInvited.isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return InviteUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteUserViewHolder, position: Int) {
        val user = users[position]
        val userInfo = String.format("%s %s", user.name, user.lastName)
//        holder.checkBoxInvited.onTouchEvent()
        holder.textViewUserInfo.text = userInfo

        holder.viewStatus.background = if (user.onlineStatus) {
            ResourcesCompat.getDrawable(holder.itemView.resources, R.drawable.oval, null)
        } else ResourcesCompat.getDrawable(holder.itemView.resources, R.drawable.oval_red, null)
        holder.checkBoxInvited.setOnClickListener {
            onItemClick?.invoke(user)
        }
        if (user.uri != "") {
            Glide.with(holder.itemView.context).load(user.uri).into(holder.imageViewUserAvatar)
        }
    }

    override fun getItemCount(): Int = users.size
}