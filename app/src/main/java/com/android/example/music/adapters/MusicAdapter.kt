package com.android.example.music.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.music.R
import com.android.example.music.databinding.ItemMusicBinding
import com.android.example.music.models.Music
import java.io.File

class MusicAdapter(private val list: List<Music>, val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MusicAdapter.Vh>() {

    inner class Vh(private val itemMusicBinding: ItemMusicBinding) :
        RecyclerView.ViewHolder(itemMusicBinding.root) {

        fun bind(music: Music, position: Int) {
            itemMusicBinding.image.setImageURI(Uri.parse(music.imagePath.toString()))
            if (itemMusicBinding.image.drawable == null) {
                itemMusicBinding.image.setImageResource(R.drawable.placeholder)
            }
            itemMusicBinding.artist.text = music.artist
            itemMusicBinding.name.text = music.name
            itemMusicBinding.name.isSelected = true
            if (position == list.size - 1) {
                itemMusicBinding.linear.visibility = View.INVISIBLE
            } else if (position == 0) {
                itemMusicBinding.linear.visibility = View.VISIBLE
            }
            itemMusicBinding.container.setOnClickListener {
                onItemClickListener.onItemListener(music)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh = Vh(
        ItemMusicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position], position)
    }

    interface OnItemClickListener {
        fun onItemListener(music: Music)
    }
}