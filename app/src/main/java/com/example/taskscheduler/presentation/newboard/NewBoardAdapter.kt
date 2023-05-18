package com.example.taskscheduler

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.squareup.picasso.Picasso

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
            return oldBoard.contentEquals(newBoard)
        }
    }

    class NewBoardAdapter: RecyclerView.Adapter<NewBoardAdapter.NewBoardViewHolder>() {
        private var selectedPosition = -1
        var onItemClick:((String) -> Unit)? = null

        var backgroundImageUrls = ArrayList<String>()
            set(newValue) {
                val diffCallback = NewBoardDiffCallback(field, newValue)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                diffResult.dispatchUpdatesTo(this)
                field = newValue
            }


        inner class NewBoardViewHolder(
            itemView: View,
            var imageViewBackground: ImageView = itemView.findViewById(R.id.imageViewBackground),
            var checkBox: CheckBox = itemView.findViewById(R.id.checkBox),
            val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.new_board_constraint)
        ) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
//                    Log.i("USER_IMAGE_BACK", backgroundImageUrls[adapterPosition])
                    onItemClick?.invoke(backgroundImageUrls[adapterPosition])
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBoardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.new_board_background_item, parent, false)
            return NewBoardViewHolder(view)
        }

        override fun getItemCount(): Int = backgroundImageUrls.size


        override fun onBindViewHolder(holder: NewBoardViewHolder, position: Int) {
            val imageUrl = backgroundImageUrls[position]
            //val boardInfo = String.format("%s", board.name)
            Log.i("USER_IMAGE_BOARD", imageUrl)
            Glide.with(holder.itemView.context).load(imageUrl).override(SIZE_ORIGINAL, SIZE_ORIGINAL).into(holder.imageViewBackground)
//            Glide.with(holder.itemView.context).load(imageUrl).into(object : CustomTarget<Drawable>() {
//                override fun onResourceReady(
//                    resource: Drawable,
//                    transition: Transition<in Drawable>?
//                ) {
//                    holder.constraintLayout.background = resource
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//            Picasso.get().load(imageUrl)
//                .placeholder(R.drawable.board_back_image_placeholder)
//                .error(R.drawable.board_back_image_placeholder)
//                .into(holder.imageViewBackground)
            holder.checkBox.isChecked = (selectedPosition == position)
            holder.checkBox.setOnClickListener(object : View.OnClickListener {


                override fun onClick(v: View?) {
                    selectedPosition = holder.adapterPosition
                    onItemClick?.invoke(backgroundImageUrls[holder.adapterPosition])
                    notifyDataSetChanged()
                }

            })

        }
    }