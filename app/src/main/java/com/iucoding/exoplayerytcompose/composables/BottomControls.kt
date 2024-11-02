import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iucoding.exoplayerytcompose.R
import com.iucoding.exoplayerytcompose.ui.theme.Purple40
import com.iucoding.exoplayerytcompose.ui.theme.Purple80

@Composable
fun BottomControls(
	modifier: Modifier = Modifier,
	totalDuration: () -> Long,
	currentTime: () -> Long,
	bufferedPercentage: () -> Int,
	onSeekChanged: (timeMs: Float) -> Unit
) {

	val duration = remember(totalDuration()) { totalDuration() }

	val videoTime = remember(currentTime()) { currentTime() }

	val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {

		Text(
			modifier = Modifier.padding(horizontal = 8.dp),
			text = videoTime.formatMinSec(),
			color = Color.White
		)

		Box(modifier = Modifier.weight(1f)) {
			Slider(
				value = buffer.toFloat(),
				enabled = false,
				onValueChange = { /*do nothing*/ },
				valueRange = 0f..100f,
				colors = SliderDefaults.colors(
					disabledThumbColor = Color.Transparent, disabledActiveTrackColor = Color.Gray
				)
			)

			Slider(
				modifier = Modifier.fillMaxWidth(),
				value = videoTime.toFloat(),
				onValueChange = onSeekChanged,
				valueRange = 0f..duration.toFloat(),
				colors = SliderDefaults.colors(
					thumbColor = Purple80, activeTickColor = Purple40
				)
			)
		}

		Text(
			modifier = Modifier.padding(horizontal = 8.dp),
			text = duration.formatMinSec(),
			color = Color.White
		)

		IconButton(
			modifier = Modifier
                .width(40.dp)
                .height(24.dp)
                .padding(horizontal = 8.dp),
			onClick = {}) {
			Image(
				contentScale = ContentScale.Crop,
				painter = painterResource(id = R.drawable.exo_styled_controls_fullscreen_enter),
				contentDescription = "Enter/Exit fullscreen"
			)
		}
	}
}

@Composable
@Preview
private fun BottomControlsPreview() {
	BottomControls(modifier = Modifier.fillMaxWidth(),
		totalDuration = { 100L },
		currentTime = { 25L },
		bufferedPercentage = { 30 },
		onSeekChanged = {})
}
