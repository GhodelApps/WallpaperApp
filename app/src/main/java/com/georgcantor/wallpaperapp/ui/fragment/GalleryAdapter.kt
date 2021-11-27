package com.georgcantor.wallpaperapp.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ItemPictureBinding
import com.georgcantor.wallpaperapp.model.remote.response.CommonPic
import com.georgcantor.wallpaperapp.util.loadImage

class GalleryAdapter(
    private val onClick: (CommonPic?) -> Unit
) : PagingDataAdapter<CommonPic, GalleryAdapter.GalleryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GalleryViewHolder(
        ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class GalleryViewHolder(
        val binding: ItemPictureBinding,
        private val onClick: (CommonPic?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pic: CommonPic) {
            itemView.context.loadImage(pic.url, binding.image, null, R.color.gray)
            itemView.setOnClickListener { onClick(pic) }
        }
    }

    companion object {
        object DiffCallback : DiffUtil.ItemCallback<CommonPic>() {
            override fun areItemsTheSame(old: CommonPic, new: CommonPic) = old == new
            override fun areContentsTheSame(old: CommonPic, new: CommonPic) = old.id == new.id
        }
    }
}