import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.iucoding.exoplayerytcompose.composables.PlayerControls
import java.util.concurrent.TimeUnit

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
	modifier: Modifier = Modifier,
	url: () -> String
) {
	val context = LocalContext.current
	val uri = remember(url()) { url() }

	val exoPlayer = remember {
		ExoPlayer.Builder(context).apply {
			setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
			setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
		}.build().apply {
			setMediaItem(MediaItem.Builder().apply {
				setUri(uri)
				setMediaMetadata(
					MediaMetadata.Builder().setDisplayTitle("Big Buck Bunny").build()
				)
			}.build())
			prepare()
			playWhenReady = true
		}
	}

	var shouldShowControls by remember { mutableStateOf(false) }

	var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

	var totalDuration by remember { mutableLongStateOf(0L) }

	var currentTime by remember { mutableLongStateOf(0L) }

	var bufferedPercentage by remember { mutableIntStateOf(0) }

	var playbackState by remember { mutableIntStateOf(exoPlayer.playbackState) }

	Box(modifier = modifier) {
		DisposableEffect(key1 = Unit) {
			val listener = object : Player.Listener {
				override fun onEvents(
					player: Player, events: Player.Events
				) {
					super.onEvents(player, events)
					totalDuration = player.duration.coerceAtLeast(0L)
					currentTime = player.currentPosition.coerceAtLeast(0L)
					bufferedPercentage = player.bufferedPercentage
					isPlaying = player.isPlaying
					playbackState = player.playbackState
				}
			}

			exoPlayer.addListener(listener)

			onDispose {
				exoPlayer.removeListener(listener)
				exoPlayer.release()
			}
		}

		AndroidView(modifier = Modifier
            .fillMaxSize()
            .clickable {
                shouldShowControls = shouldShowControls.not()
            }, factory = {
			PlayerView(context).apply {
				player = exoPlayer
				useController = false
				layoutParams = FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)
			}
		})

		PlayerControls(modifier = Modifier.fillMaxSize(),
			isVisible = { shouldShowControls },
			isPlaying = { isPlaying },
			title = { exoPlayer.mediaMetadata.displayTitle.toString() },
			playbackState = { playbackState },
			onReplayClick = { exoPlayer.seekBack() },
			onForwardClick = { exoPlayer.seekForward() },
			onPauseToggle = {
				when {
					exoPlayer.isPlaying -> {
						// pause the video
						exoPlayer.pause()
					}

					exoPlayer.isPlaying.not() && playbackState == STATE_ENDED -> {
						exoPlayer.seekTo(0)
						exoPlayer.playWhenReady = true
					}

					else -> {
						// play the video
						// it's already paused
						exoPlayer.play()
					}
				}
				isPlaying = isPlaying.not()
			},
			totalDuration = { totalDuration },
			currentTime = { currentTime },
			bufferedPercentage = { bufferedPercentage },
			onSeekChanged = { timeMs: Float ->
				exoPlayer.seekTo(timeMs.toLong())
			})
	}
}

@SuppressLint("DefaultLocale")
fun Long.formatMinSec(): String {
	return if (this == 0L) {
		"..."
	} else {
		String.format(
			"%02d:%02d",
			TimeUnit.MILLISECONDS.toMinutes(this),
			TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
				TimeUnit.MILLISECONDS.toMinutes(this)
			)
		)
	}
}

private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds
