package com.android.example.music

import android.Manifest
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.example.music.adapters.MusicAdapter
import com.android.example.music.databinding.FragmentListBinding
import com.android.example.music.db.MyDbHelper
import com.android.example.music.models.Music
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var list: ArrayList<Music>
    private lateinit var adapter: MusicAdapter
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var myDbHelper: MyDbHelper
    private lateinit var allMusics: ArrayList<Music>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        myDbHelper = MyDbHelper(requireContext())
        checkPermission()
        allMusics = ArrayList(myDbHelper.getAllMusics())
        if (allMusics.isEmpty()) {
            musicFiles()
        }

        adapter = MusicAdapter(allMusics, object : MusicAdapter.OnItemClickListener {
            override fun onItemListener(music: Music) {
                val bundle = Bundle()
                bundle.putSerializable("music", music)
                findNavController().navigate(R.id.musicFragment, bundle)
            }
        })

        binding.rv.adapter = adapter
        return binding.root
    }

    private fun musicFiles(): ArrayList<Music> {
        allMusics = ArrayList()
        // Get the external storage media store audio uri
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        //val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

        // IS_MUSIC : Non-zero if the audio file is music
        val selection = MediaStore.Audio.Media._ID + " != 0"

        val cursorCols = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        // Sort the musics
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        //val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"

        // Query the external storage for music files
        val cursor = requireContext().contentResolver.query(
            uri, // Uri
            cursorCols, // Projection
            selection, // Selection
            null, // Selection arguments
            sortOrder // Sort order
        )

        // If query result is not empty

        while (cursor!!.moveToNext()) {
            val artist =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
            val track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val albumId =
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val duration =
                cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId)
            val music = Music(
                imagePath = albumArtUri.toString(),
                musicPath = data,
                name = track,
                album = album,
                artist = artist,
                duration = duration
            )
            myDbHelper.loadMusic(music)
//            list.add(music)
        }
        return allMusics
    }

    private fun releaseMp() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun checkPermission() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                }

            }).check()
    }
}