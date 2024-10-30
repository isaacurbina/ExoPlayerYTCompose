package com.iucoding.exoplayerytcompose.composables

sealed interface PlayerAction {

	data object Rewind : PlayerAction
	data object PlayPause : PlayerAction
	data object Forward : PlayerAction
}
