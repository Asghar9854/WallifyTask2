package com.example.wallifytask2.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallifytask2.dataBase.savePhoto
import com.example.wallifytask2.databinding.ItemPhotoBinding
import com.example.wallifytask2.utils.WALLPAPER_OBJ

class SavedAdapter(private val photos: List<savePhoto>) :
    RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {
    class SavedViewHolder(var binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: savePhoto) {
            binding.photographerTextView.text = photo.name
            binding.root.context?.let { context ->
                Glide.with(context).load(photo.imagePath).into(binding.photoImageView)

                binding.root.setOnClickListener {
//                    context.startActivity(
//                        Intent(context, EditActivity::class.java).putExtra(WALLPAPER_OBJ, photo)
//                    )
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) =
        holder.bind(photos[position])

    override fun getItemCount(): Int = photos.size
}