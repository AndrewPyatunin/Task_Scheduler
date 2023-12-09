package com.example.taskscheduler.presentation.newboard

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.DiffCallback

class NewBoardDiffCallback(
    private val oldList: List<String>,
    private val newList: List<String>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldBoard = oldList[oldItemPosition]
        val newBoard = newList[newItemPosition]
        return oldBoard == newBoard
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldBoard = oldList[oldItemPosition]
        val newBoard = newList[newItemPosition]
        return oldBoard === newBoard
    }
}

class NewBoardAdapter(context: Context) :
    RecyclerView.Adapter<NewBoardAdapter.NewBoardViewHolder>() {

    private var selectedPosition = -1
    var urlBackground: String = ""
    var onItemClick: ((BackgroundImage) -> Unit)? = null

    var backgroundImageUrls = ArrayList<BackgroundImage>()
        set(newValue) {
            val diffCallback = DiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            field = newValue
        }


    inner class NewBoardViewHolder(
        itemView: View,
        var progressBar: ProgressBar = itemView.findViewById(R.id.loadingIndicatorBackImage),
        var imageViewBackground: ImageView = itemView.findViewById(R.id.imageViewBackground),
        var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val item = backgroundImageUrls[adapterPosition]
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBoardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_board_background_item, parent, false)
        return NewBoardViewHolder(view)
    }

    override fun getItemCount(): Int = backgroundImageUrls.size

    override fun onBindViewHolder(holder: NewBoardViewHolder, position: Int) {
        val backgroundImage = backgroundImageUrls[position]
        Log.i("USER_WORKKKKKing", "$position")
        Log.i("USER_IMAGE_BOARD", backgroundImage.imageUrl)
        Glide.with(holder.itemView.context).load(backgroundImage.imageUrl).override(
            SIZE_ORIGINAL, SIZE_ORIGINAL
        ).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                holder.imageViewBackground.setImageDrawable(resource)
                holder.imageViewBackground.visibility = View.VISIBLE
                holder.checkBox.visibility = View.VISIBLE
                holder.progressBar.visibility = View.GONE
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                holder.imageViewBackground.setImageDrawable(placeholder)
            }

        })


        holder.checkBox.isChecked = (selectedPosition == position)
        click(holder.imageViewBackground, holder)
        click(holder.checkBox, holder)
        if (urlBackground == backgroundImage.imageUrl) {
            holder.checkBox.isChecked = true
        }
    }

    private fun click(view: View, holder: NewBoardViewHolder) {
        view.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                urlBackground = ""
                selectedPosition = holder.adapterPosition
                onItemClick?.invoke(backgroundImageUrls[holder.adapterPosition])
                notifyDataSetChanged()
            }
        })
    }
}