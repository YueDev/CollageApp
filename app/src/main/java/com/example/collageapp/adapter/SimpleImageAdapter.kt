package com.example.collageapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.collageapp.R

class SimpleImageAdapter(
    val itemClick: (position: Int, resId: Int) -> Unit
) : ListAdapter<Int, SimpleImageHolder>(SimpleIntDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleImageHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_single_image, parent, false)
        val holder = SimpleImageHolder(itemView)
        itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position < 0 || position >= itemCount) return@setOnClickListener
            itemClick(position, getItem(position))
        }
        return holder
    }

    override fun onBindViewHolder(holder: SimpleImageHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class SimpleIntDiffCallback : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}

class SimpleImageHolder(view: View) : ViewHolder(view) {

    private val imageView: ImageView = itemView.findViewById(R.id.image_view_single)

    fun bind(resId: Int) {
        imageView.setImageResource(resId)
    }

}