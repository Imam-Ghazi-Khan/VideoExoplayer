package com.igk.videoexoplayer

import android.content.Context
import android.net.Uri
import android.net.Uri.*
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.size
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.igk.videoexoplayer.ui.theme.VideoExoplayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoExoplayerTheme {
                    JsonParse("https://pixabay.com/api/videos/?key=30333777-9eaa2f740e1b16ef7ad210e43&amp;q=yellow+flowers")
            }
        }
    }
}

@Composable
fun JsonParse(url: String) {
    val context = LocalContext.current
    val queue = Volley.newRequestQueue(context)
    var text by remember {
        mutableStateOf("https://cdn.pixabay.com/vimeo/755291766/Flower%20-%20132927.mp4?width=1920&hash=45b3762825b17ba418232ef232977cb84441de7fd")
    }

    var videoList = remember{mutableListOf(text)}


// Request a string response from the provided URL.
    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
        { response ->
             var arr = response.getJSONArray("hits")
            for(i in 0..arr.length()-1) {
                text = arr.getJSONObject(i).getJSONObject("videos").getJSONObject("large")
                    .getString("url")
                videoList.add(i,text)
            }
        },
        { Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show() })

    queue.add(jsonObjectRequest)

    PlayVideo(urlList = videoList, context = context)

}


@Composable
fun PlayVideo(urlList: MutableList<String>, context:Context){

    var currentItemIndex by remember {
        mutableStateOf(0)
    }

    val player = remember{ ExoPlayer.Builder(context).build()}

    LazyColumn{
        items(urlList) {
                url ->

                val player = remember{
                    ExoPlayer.Builder(context).build().apply {
                        val dataSource = DefaultDataSource.Factory(context)
                        val source = ProgressiveMediaSource.Factory(dataSource)
                            .createMediaSource(MediaItem.fromUri(parse(url)))

                        addMediaSource(source)
                        prepare()
                    }
                }

                AndroidView(factory = {
                    PlayerView(it).apply{
                        this.player = player
                        //player.playWhenReady = true
                        useController = true
                        setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)

                    }
                },
                modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp))
            player.pause()
        }
    }

/*
    Column(modifier = Modifier.fillMaxSize()) {
        val player = remember{
            ExoPlayer.Builder(context).build().apply {
                val dataSource = DefaultDataSource.Factory(context)
                val source = ProgressiveMediaSource.Factory(dataSource)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

                addMediaSource(source)
                prepare()
            }
        }

        AndroidView(factory = {
            PlayerView(it).apply{
                this.player = player
                player.playWhenReady = true
                useController = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            }
        })

    }*/

}


