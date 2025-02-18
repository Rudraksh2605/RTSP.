package com.example.rtsp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import org.videolan.libvlc.Media
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView


    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer


    private lateinit var urlInput: EditText
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        playerView = findViewById(R.id.playerView)
        urlInput = findViewById(R.id.urlInput)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)


        player = ExoPlayer.Builder(this).build()
        playerView.player = player


        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)


        playButton.setOnClickListener { playStream() }
        pauseButton.setOnClickListener { pauseStream() }
        stopButton.setOnClickListener { stopStream() }
    }

    private fun playStream() {
        val rtspUrl = urlInput.text.toString()
        if (rtspUrl.isNotEmpty()) {
            try {
                val media = Media(libVLC, Uri.parse(rtspUrl))

                // VLC RTSP options for better streaming
                media.addOption(":rtsp-tcp")  // Force TCP (better compatibility)
                media.addOption(":network-caching=1500")  // Reduce buffering delay
                media.addOption(":live-caching=1000")
                media.addOption(":clock-jitter=0")
                media.addOption(":clock-synchro=0")

                mediaPlayer.media = media
                media.release()  // Avoid memory leaks
                mediaPlayer.play()

                Log.d("RTSP", "Playing stream: $rtspUrl")
            } catch (e: Exception) {
                Log.e("RTSP Error", "Error playing RTSP stream", e)
                Toast.makeText(this, "Failed to play stream", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter a valid RTSP URL", Toast.LENGTH_SHORT).show()
        }
    }


    private fun pauseStream() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    private fun stopStream() {
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        libVLC.release()
    }
}
