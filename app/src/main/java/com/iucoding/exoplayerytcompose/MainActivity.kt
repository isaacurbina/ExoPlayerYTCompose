package com.iucoding.exoplayerytcompose

import CustomExoPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.iucoding.exoplayerytcompose.ui.theme.ExoplayerYTComposeTheme

private val videoUrls = listOf(
	"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", // dash
	"https://assets.afcdn.com/video49/20210722/v_645516.m3u8", // hls
	"https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4" // mp4
)

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ExoplayerYTComposeTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					CustomExoPlayer(modifier = Modifier
                        .background(Color.Black)
                        .padding(innerPadding)
                        .fillMaxSize(), url = { videoUrls[0] })
				}
			}
		}
	}
}
