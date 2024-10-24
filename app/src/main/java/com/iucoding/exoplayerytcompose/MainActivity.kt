package com.iucoding.exoplayerytcompose

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
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
						modifier = Modifier.padding(innerPadding)
					)
				}
			}
		}
	}
}

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerDemo(modifier: Modifier = Modifier) {
	val context = LocalContext.current
	val activity = LocalContext.current as Activity
	var player: Player? by remember { mutableStateOf(null) }

	val enterFullScreen = { activity.requestedOrientation == SCREEN_ORIENTATION_USER_LANDSCAPE }
	val exitFullScreen = { activity.requestedOrientation == SCREEN_ORIENTATION_USER }

	activity.requestedOrientation = SCREEN_ORIENTATION_USER
	val playerView = createPlayerView(player).apply {
		controllerAutoShow = true
		keepScreenOn = true
		setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
		setFullscreenButtonClickListener { isFullScreen ->
			if (isFullScreen) {
				if (activity.requestedOrientation == SCREEN_ORIENTATION_USER) {
					enterFullScreen()
				}
			} else {
				exitFullScreen()
			}
		}
	}
	ComposableLifecycle { _, event ->
		when (event) {
			Lifecycle.Event.ON_RESUME -> {
				player = initPlayer(context)
				playerView.onResume()
			}

			Lifecycle.Event.ON_PAUSE -> {
				playerView.apply {
					onPause()
					player?.release()
					player = null
				}
			}

			else -> {}
		}
	}

	AndroidView(modifier = modifier, factory = { playerView })
}

@OptIn(UnstableApi::class)
private fun initPlayer(context: Context): Player {
	return ExoPlayer.Builder(context).build().apply {
		val defaultHttpDataSource = DefaultHttpDataSource.Factory()
		val uri = Uri.parse(videoUrl)
		val mediaSource = buildMediaSource(uri, defaultHttpDataSource, null)
		setMediaSource(mediaSource)
		playWhenReady = true
		prepare()
	}
}

@OptIn(UnstableApi::class)
private fun buildMediaSource(
	uri: Uri, defaultHttpDataSource: DefaultHttpDataSource.Factory, exception: String?
): MediaSource {
	val type = Util.inferContentType(uri, exception)
	return when (type) {
		C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(defaultHttpDataSource).createMediaSource(
			MediaItem.fromUri(uri)
		)

		C.CONTENT_TYPE_SS -> SsMediaSource.Factory(defaultHttpDataSource).createMediaSource(
			MediaItem.fromUri(uri)
		)

		C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(defaultHttpDataSource).createMediaSource(
			MediaItem.fromUri(uri)
		)

		C.CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(defaultHttpDataSource)
			.createMediaSource(
				MediaItem.fromUri(uri)
			)

		else -> {
			throw IllegalStateException("Unsupported type $type")
		}
	}
}

@Composable
fun ComposableLifecycle(
	lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
	onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
	DisposableEffect(key1 = lifecycleOwner) {
		// 1. Create a lifecycle event observer to handle lifecycle events
		val observer = LifecycleEventObserver { source, event ->
			// 2. Call the provided onEvent callback with the source and event
			onEvent(source, event)
		}
		// 3. Add the observer to the lifecycle of the provided lifecycle owner
		lifecycleOwner.lifecycle.addObserver(observer)
		// 4. Remove the observer when the composable is disposed
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
		}
	}
}

@Composable
private fun createPlayerView(player: Player?): PlayerView {
	val context = LocalContext.current
	val playerView = remember {
		PlayerView(context).apply {
			this.player = player
		}
	}
	DisposableEffect(player) {
		playerView.player = player

		onDispose {
			playerView.player = null
		}
	}
	return playerView
}
