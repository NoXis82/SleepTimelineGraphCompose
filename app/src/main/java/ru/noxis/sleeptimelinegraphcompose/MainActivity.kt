package ru.noxis.sleeptimelinegraphcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Instant
import kotlinx.datetime.UtcOffset
import ru.noxis.sleeptimelinegraphcompose.components.App
import ru.noxis.sleeptimelinegraphcompose.components.SleepSessionCanvas
import ru.noxis.sleeptimelinegraphcompose.model.SleepSessionRecord
import ru.noxis.sleeptimelinegraphcompose.model.SleepSessionStageType
import ru.noxis.sleeptimelinegraphcompose.ui.theme.SleepTimelineGraphComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SleepTimelineGraphComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SleepTimelineGraphComposeTheme {
        App()
    }
}