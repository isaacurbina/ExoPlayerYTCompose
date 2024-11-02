import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.iucoding.exoplayerytcompose.R

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun CustomExoPlayer(
	modifier: Modifier = Modifier, url: () -> String
) {
	val context = LocalContext.current
	val activity = (LocalContext.current as? Activity)

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


	var lifecycle by remember {
		mutableStateOf(Lifecycle.Event.ON_CREATE)
	}
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(key1 = lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			lifecycle = event
		}
		lifecycleOwner.lifecycle.addObserver(observer = observer)
		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer = observer)
		}
	}

	Box(modifier = modifier) {
		DisposableEffect(key1 = Unit) {
			onDispose {
				exoPlayer.release()
			}
		}

		AndroidView(modifier = Modifier.fillMaxSize(), factory = {
			PlayerView(context).apply {
				player = exoPlayer
				useController = true
				setControllerHideDuringAds(true)
				layoutParams = FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
				)
				findViewById<View>(R.id.ua_back).setOnClickListener {
					activity?.finish()
				}
				findViewById<View>(R.id.ua_home).setOnClickListener {
					activity?.run {
						setResult(123, Intent("Home"))
						finish()
					}

				}
			}
		}, update = {
			when (lifecycle) {
				Lifecycle.Event.ON_PAUSE -> {
					it.player?.pause()
					it.onPause()
				}

				Lifecycle.Event.ON_RESUME -> {
					it.onResume()
					it.player?.play()
				}

				else -> Unit
			}
		})
	}
}

private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds
