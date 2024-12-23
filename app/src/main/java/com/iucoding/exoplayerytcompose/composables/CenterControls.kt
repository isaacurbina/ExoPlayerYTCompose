package com.iucoding.exoplayerytcompose.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player.STATE_ENDED
import com.iucoding.exoplayerytcompose.R

@Composable
fun CenterControls(
	modifier: Modifier = Modifier,
	isPlaying: () -> Boolean,
	playbackState: () -> Int,
	onReplayClick: () -> Unit,
	onPauseToggle: () -> Unit,
	onForwardClick: () -> Unit
) {
	val isVideoPlaying = remember(isPlaying()) { isPlaying() }

	val playerState = remember(playbackState()) { playbackState() }

	Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
		IconButton(modifier = Modifier.size(40.dp), onClick = onReplayClick) {
			Image(
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop,
				painter = painterResource(id = R.drawable.exo_styled_controls_simple_rewind),
				contentDescription = "Replay 5 seconds"
			)
		}

		IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
			Image(
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop,
				painter = when {
					isVideoPlaying -> {
						painterResource(id = R.drawable.exo_styled_controls_pause)
					}

					isVideoPlaying.not() && playerState == STATE_ENDED -> {
						painterResource(id = R.drawable.exo_styled_controls_simple_rewind)
					}

					else -> {
						painterResource(id = R.drawable.exo_styled_controls_play)
					}
				},
				contentDescription = "Play/Pause"
			)
		}

		IconButton(modifier = Modifier.size(40.dp), onClick = onForwardClick) {
			Image(
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop,
				painter = painterResource(id = R.drawable.exo_styled_controls_simple_fastforward),
				contentDescription = "Forward 10 seconds"
			)
		}
	}
}
