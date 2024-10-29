package com.iucoding.exoplayerytcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.iucoding.exoplayerytcompose.composables.ExoPlayerDemo
import com.iucoding.exoplayerytcompose.ui.theme.ExoplayerYTComposeTheme

private const val videoUrl =
	"https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
private const val videoUrlHtls = "https://assets.afcdn.com/video49/20210722/v_645516.m3u8"

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ExoplayerYTComposeTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					ExoPlayerDemo(
						modifier = Modifier.padding(innerPadding),
						url = { videoUrl }
					)
				}
			}
		}
	}
}

