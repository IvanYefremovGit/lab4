package com.example.lab4

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.MediaItem
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private val PICK_FILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        val playBtn = findViewById<Button>(R.id.playBtn)
        val pauseBtn = findViewById<Button>(R.id.pauseBtn)
        val stopBtn = findViewById<Button>(R.id.stopBtn)
        val selectFileBtn = findViewById<Button>(R.id.selectFileBtn)
        val playUrlBtn = findViewById<Button>(R.id.playUrlBtn)
        val urlInput = findViewById<EditText>(R.id.urlInput)

        initPlayer()

        playBtn.setOnClickListener {
            player.play()
        }

        pauseBtn.setOnClickListener {
            player.pause()
        }

        stopBtn.setOnClickListener {
            player.stop()
            player.clearMediaItems()
            urlInput.setText("")
            resetPlayer()
        }

        selectFileBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

        playUrlBtn.setOnClickListener {
            val url = urlInput.text.toString()

            if (url.isEmpty()) {
                Toast.makeText(this, "Введи URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!url.startsWith("http")) {
                Toast.makeText(this, "Невірне посилання", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetPlayer()

            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        player.addListener(object : Player.Listener {

            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(
                    this@MainActivity,
                    "Помилка: не вдалося відтворити файл",
                    Toast.LENGTH_LONG
                ).show()

                resetPlayer()
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_IDLE) {
                    Toast.makeText(
                        this@MainActivity,
                        "Немає даних для відтворення",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun resetPlayer() {
        player.release()
        initPlayer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                resetPlayer()

                val mediaItem = MediaItem.fromUri(uri)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}