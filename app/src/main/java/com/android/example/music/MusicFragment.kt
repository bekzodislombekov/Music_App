package com.android.example.music

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.example.music.databinding.FragmentMusicBinding
import com.android.example.music.db.MyDbHelper
import com.android.example.music.models.Music


private const val ARG_PARAM1 = "music"

class MusicFragment : Fragment() {
    private var music: Music? = null
    private lateinit var binding: FragmentMusicBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler
    private lateinit var myDbHelper: MyDbHelper
    private lateinit var allMusics: ArrayList<Music>
    private lateinit var selectedMusic: Music

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            music = it.getSerializable(ARG_PARAM1) as Music?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicBinding.inflate(layoutInflater, container, false)
        myDbHelper = MyDbHelper(requireContext())
        handler = Handler(Looper.getMainLooper())
        allMusics = ArrayList(myDbHelper.getAllMusics())
        for (allMusic in allMusics) {
            if (allMusic.name == music?.name) {
                selectedMusic = allMusic
                break
            }
        }
        playMusic(selectedMusic.musicPath)
        loadDataToViews(selectedMusic)

        val duration = mediaPlayer?.duration
        binding.slider.valueTo = duration!!.toFloat()
        setDuration(duration)

        binding.skipNext.setOnClickListener {
            val id = selectedMusic.id + 1
            if (allMusics.size > selectedMusic.id) {
                for (allMusic in allMusics) {
                    if (allMusic.id == id) {
                        selectedMusic = allMusic
                        break
                    }
                }
                releaseMp()
                playMusic(selectedMusic.musicPath)
                loadDataToViews(selectedMusic)
                setDuration(mediaPlayer!!.duration)
            } else {
                for (allMusic in allMusics) {
                    if (allMusic.id == 1) {
                        selectedMusic = allMusic
                        break
                    }
                }
                releaseMp()
                playMusic(selectedMusic.musicPath)
                loadDataToViews(selectedMusic)
                setDuration(mediaPlayer!!.duration)
            }
        }

        binding.forward.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(30000)!!)
        }

        binding.backward.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.minus(30000)!!)
        }

        binding.skipPrevious.setOnClickListener {
            val id = selectedMusic.id - 1
            if (id > 0) {
                for (allMusic in allMusics) {
                    if (allMusic.id == id) {
                        selectedMusic = allMusic
                        break
                    }
                }
                releaseMp()
                playMusic(selectedMusic.musicPath)
                loadDataToViews(selectedMusic)
                setDuration(mediaPlayer!!.duration)
            } else {
                val lastId = allMusics.size
                for (allMusic in allMusics) {
                    if (allMusic.id == lastId) {
                        selectedMusic = allMusic
                        break
                    }
                }
                releaseMp()
                playMusic(selectedMusic.musicPath)
                loadDataToViews(selectedMusic)
                setDuration(mediaPlayer!!.duration)
            }
        }

        binding.more.setOnClickListener {
            findNavController().popBackStack()
        }

        handler.postDelayed(runnable, 100)

        binding.shape.setOnClickListener {
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.pause()
                binding.playBtn.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()
                binding.playBtn.setImageResource(R.drawable.ic_pause)
            }
        }

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                mediaPlayer?.seekTo(value.toInt())
            }
        }
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        releaseMp()
        handler.removeCallbacksAndMessages(null)
    }

    private fun playMusic(path: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    private val runnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer?.currentPosition
            binding.slider.value = currentPosition!!.toFloat()
            val duration = currentPosition / 1000
            val min = duration / 60
            val sec = duration % 60
            if (min < 10) {
                binding.timeMin.text = "0$min"
            } else {
                binding.timeMin.text = "$min"
            }
            if (sec < 10) {
                binding.timeSec.text = "0$sec"
            } else {
                binding.timeSec.text = "$sec"
            }

            if (binding.timeMin.text == binding.durationMin.text && binding.timeSec.text == binding.durationSec.text) {
                val id = selectedMusic.id + 1
                if (allMusics.size != selectedMusic.id) {
                    for (allMusic in allMusics) {
                        if (allMusic.id == id) {
                            selectedMusic = allMusic
                            break
                        }
                    }
                    binding.slider.value = 0.1F
                    releaseMp()
                    playMusic(selectedMusic.musicPath)
                    loadDataToViews(selectedMusic)
                    setDuration(mediaPlayer!!.duration)
                }
            }
            Log.d("TTT", mediaPlayer?.duration.toString())
            Log.d("TTT", mediaPlayer?.currentPosition.toString())
            handler.postDelayed(this, 100)
        }
    }

    private fun loadDataToViews(music: Music) {
        binding.backImg.setImageURI(Uri.parse(music.imagePath))
        binding.image.setImageURI(Uri.parse(music.imagePath))
        if (binding.image.drawable == null) {
            binding.image.setImageResource(R.drawable.placeholder)
        }
        binding.name.text = music.name
        binding.artist.text = music.artist
        binding.currentPos.text = music.id.toString()
        binding.listSize.text = "/${allMusics.size}"
        binding.slider.valueTo = mediaPlayer?.duration!!.toFloat()
        binding.playBtn.setImageResource(R.drawable.ic_pause)
    }

    private fun setDuration(duration: Int) {
        val durationSec = duration / 1000
        val sec = durationSec % 60
        val min = durationSec / 60
        if (min < 10) {
            binding.durationMin.text = "0$min"
        } else {
            binding.durationMin.text = "$min"
        }
        if (sec < 10) {
            binding.durationSec.text = "0$sec"
        } else {
            binding.durationSec.text = "$sec"
        }
    }

    private fun releaseMp() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.release()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}