package com.evitalz.homevitalz.heartskan.ui.fragments.analytics

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.M)
class LoopMediaPlayer private constructor(context: Context, resId: Int, speed: Float) {
    private var mContext: Context? = null
    private var mResId = 0
    private var mCounter = 1
    private var speed = 1f
    var mCurrentPlayer: MediaPlayer? = null
    private var mNextPlayer: MediaPlayer? = null

    fun stop() {
        try {
            if (mCurrentPlayer != null && mCurrentPlayer!!.isPlaying) {
                mCurrentPlayer!!.stop()
                mCurrentPlayer!!.release()
                mCurrentPlayer = null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            mCurrentPlayer = null
        }
    }

    fun start() {
        try {
            if (mCurrentPlayer != null) {
                mCurrentPlayer!!.start()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            mCurrentPlayer = null
        }
    }

    fun release() {
        try {
            if (mCurrentPlayer != null && mCurrentPlayer!!.isPlaying) {
                mCurrentPlayer!!.start()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            mCurrentPlayer = null
        }
    }

    private fun createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer!!.setNextMediaPlayer(mNextPlayer)
        mCurrentPlayer!!.setOnCompletionListener(onCompletionListener)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val onCompletionListener =
        MediaPlayer.OnCompletionListener { mediaPlayer ->
            if (mCurrentPlayer != null) {
                try {
                    mediaPlayer.release()
                    mCurrentPlayer = mNextPlayer
                    mCurrentPlayer!!.playbackParams =
                        mCurrentPlayer!!.playbackParams.setSpeed(speed)
                    createNextMediaPlayer()
                } catch (e1: Exception) {
                    e1.printStackTrace()
                    mCurrentPlayer = null
                }
            }
            Log.d(TAG, String.format("Loop #%d", ++mCounter))
        }

    companion object {
        val TAG = LoopMediaPlayer::class.java.simpleName
        fun create(context: Context, resId: Int, speed: Float): LoopMediaPlayer {
            return LoopMediaPlayer(context, resId, speed)
        }
    }

    init {
        mContext = context
        mResId = resId
        this.speed = speed
        mCurrentPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer!!.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            mCurrentPlayer!!.setPlaybackParams(mCurrentPlayer!!.getPlaybackParams().setSpeed(speed))
            mCurrentPlayer!!.start()
        })
        createNextMediaPlayer()
    }
}