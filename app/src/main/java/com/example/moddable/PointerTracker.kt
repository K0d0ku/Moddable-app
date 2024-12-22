package com.example.moddable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.zIndex

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PointerTracker(navController: NavHostController) {
    var pointerPosition by remember { mutableStateOf(Pair(0f, 0f)) } // Track x, y position
    val coordinatesList = remember { mutableStateListOf<Pair<Float, Float>>() } // List to hold the coordinate history
    var offsetY by remember { mutableStateOf(0f) } // Track vertical offset for the coordinates box

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    // Update the pointer position on drag
                    pointerPosition = Pair(pointerPosition.first + dragAmount.x, pointerPosition.second + dragAmount.y)
                    coordinatesList.add(0, pointerPosition) // Add the new coordinates at the top

                    // Limit the list to the last 10 coordinates
                    if (coordinatesList.size > 10) {
                        coordinatesList.removeLast()
                    }

                    // Update the vertical offset to move the coordinate box up
                    if (coordinatesList.size > 1) {
                        offsetY = (coordinatesList.size * 30).toFloat() // Adjust the value to control the speed of upward movement
                    }
                }
            }
    ) {
        // Header for the Pointer Tracker Tool
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Align the content at the top
        ) {
            Text(
                "Pointer Tracker Tool", style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp) // Padding between text and coordinates
            )
        }

        // Coordinates list shown in a scrollable view, bound within a fixed height box
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                .padding(bottom = 80.dp) // Add bottom padding to move the coordinates box upwards
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .zIndex(1f) // Ensure the coordinates box stays above other elements
            ) {
                items(coordinatesList) { coordinate ->
                    Text(
                        text = "Pointer Position: (${coordinate.first.toInt()}, ${coordinate.second.toInt()})",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Back button to navigate back to ToolsPage
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom, // Place the button at the bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                navController.popBackStack() // This pops the current screen and goes back
            }) {
                Text("Back to Tools")
            }
        }
    }
}