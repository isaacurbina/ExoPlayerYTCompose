package com.iucoding.exoplayerytcompose.composables

import BottomControls
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PlayerControls(
	modifier: Modifier = Modifier,
	isVisible: () -> Boolean,
	isPlaying: () -> Boolean,
	title: () -> String,
	onReplayClick: () -> Unit,
	onForwardClick: () -> Unit,
	onPauseToggle: () -> Unit,
	totalDuration: () -> Long,
	currentTime: () -> Long,
	bufferedPercentage: () -> Int,
	playbackState: () -> Int,
	onSeekChanged: (timeMs: Float) -> Unit
) {

	val visible = remember(isVisible()) { isVisible() }

	AnimatedVisibility(
		modifier = modifier, visible = visible, enter = fadeIn(), exit = fadeOut()
	) {
		Box(modifier = Modifier.fillMaxSize()) {
			TopControl(
				modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .animateEnterExit(
                        enter = slideInVertically(initialOffsetY = { fullHeight: Int ->
                            -fullHeight
                        }),
                        exit = slideOutVertically(targetOffsetY = { fullHeight: Int ->
                            -fullHeight
                        })
                    ),
				title = title
			)

			CenterControls(
				modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
				isPlaying = isPlaying,
				onReplayClick = onReplayClick,
				onForwardClick = onForwardClick,
				onPauseToggle = onPauseToggle,
				playbackState = playbackState
			)

			BottomControls(
				modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .animateEnterExit(
                        enter = slideInVertically(initialOffsetY = { fullHeight: Int ->
                            fullHeight
                        }),
                        exit = slideOutVertically(targetOffsetY = { fullHeight: Int ->
                            fullHeight
                        })
                    ),
				totalDuration = totalDuration,
				currentTime = currentTime,
				bufferedPercentage = bufferedPercentage,
				onSeekChanged = onSeekChanged
			)
		}
	}
}
