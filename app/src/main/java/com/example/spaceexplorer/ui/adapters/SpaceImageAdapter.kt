package com.example.spaceexplorer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.ItemSpaceImageBinding
import com.example.spaceexplorer.models.AstronomyPicture

/**
 * Adapter for displaying space images in a RecyclerView
 */
class SpaceImageAdapter(
    private val onItemClick: (AstronomyPicture) -> Unit
) : ListAdapter<AstronomyPicture, SpaceImageAdapter.SpaceImageViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceImageViewHolder {
        val binding = ItemSpaceImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpaceImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpaceImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SpaceImageViewHolder(
        private val binding: ItemSpaceImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(picture: AstronomyPicture) {
            binding.apply {
                // Set title and date
                titleTextView.text = picture.title
                dateTextView.text = picture.date
                
                // Set description (shortened)
                descriptionTextView.text = picture.getShortExplanation()
                
                // Load image with Glide
                if (picture.isImage()) {
                    Glide.with(imageView)
                        .load(picture.url)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(imageView)
                } else {
                    // For videos, show a video placeholder
                    Glide.with(imageView)
                        .load(R.drawable.ic_video_placeholder)
                        .centerCrop()
                        .into(imageView)
                }
                
                // Show copyright if available
                if (picture.copyright != null) {
                    copyrightTextView.text = picture.getFormattedCopyright()
                    copyrightTextView.visibility = android.view.View.VISIBLE
                } else {
                    copyrightTextView.visibility = android.view.View.GONE
                }
            }
        }
    }

    companion object {
        // DiffUtil callback for efficient RecyclerView updates
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AstronomyPicture>() {
            override fun areItemsTheSame(oldItem: AstronomyPicture, newItem: AstronomyPicture): Boolean {
                return oldItem.date == newItem.date && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: AstronomyPicture, newItem: AstronomyPicture): Boolean {
                return oldItem == newItem
            }
        }
    }
}