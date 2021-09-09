package com.android.example.music.models

import android.net.Uri
import java.io.Serializable

data class Music(
    var id: Int = 0,
    val imagePath: String,
    val musicPath: String,
    val name: String,
    val album: String,
    val artist: String,
    val duration: Int
) : Serializable