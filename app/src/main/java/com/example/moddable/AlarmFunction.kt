package com.example.moddable

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import java.util.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
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

@Composable
fun AlarmFunction(alarm: Context, alarmHour: Int, alarmMinute: Int) {
    var isAlarmRinging by remember { mutableStateOf(false) }
    var numberColor by remember { mutableStateOf(Color.Black) }
    val context = LocalContext.current
    var job: Job? by remember { mutableStateOf(null) }
    val remainingTime = remember { mutableStateOf(0) }

    // Launch the background check for alarm time
    LaunchedEffect(Unit) {
        job = launch {
            while (true) {
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)
                val currentSecond = calendar.get(Calendar.SECOND)

                // Calculate the time difference in seconds
                val alarmTimeInMillis = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, alarmHour)
                    set(Calendar.MINUTE, alarmMinute)
                    set(Calendar.SECOND, 0)
                }.timeInMillis

                val currentTimeInMillis = System.currentTimeMillis()
                remainingTime.value = ((alarmTimeInMillis - currentTimeInMillis) / 1000).toInt()

                // Trigger the alarm when the time matches
                if (remainingTime.value <= 0 && !isAlarmRinging) {
                    isAlarmRinging = true
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(alarm, "Alarm Ringing!", Toast.LENGTH_SHORT).show()
                    }
                    while (isAlarmRinging) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(alarm, "Alarm Ringing!", Toast.LENGTH_SHORT).show()
                        }
                        kotlinx.coroutines.delay(1000)
                    }
                }

                // Update UI every second
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    // Display UI based on the alarm's state
    if (!isAlarmRinging) {
        Box(
            modifier = Modifier
                .fillMaxWidth() // Stretch horizontally
                .padding(50.dp) // Optional padding around the text
                .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium) // Dark background with opacity
        ) {
            val hoursLeft = remainingTime.value / 3600
            val minutesLeft = (remainingTime.value % 3600) / 60
            val secondsLeft = remainingTime.value % 60

            Text(
                text = "Time Left: $hoursLeft:${String.format("%02d", minutesLeft)}:${String.format("%02d", secondsLeft)}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center) // Align the text in the center of the Box
            )
        }
    }
    if (isAlarmRinging) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Alarm is ringing! Press stop to stop it.",
                color = Color.White, // Change text color to white
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    isAlarmRinging = false
                    job?.cancel() // Cancel the job when the alarm is stopped
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary) // Use primary color from MaterialTheme
            ) {
                Text("Stop Alarm", color = MaterialTheme.colors.onPrimary) // Use onPrimary for text color
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Alarm is set for $alarmHour:$alarmMinute",
                color = Color.White // Change text color to white
            )
        }
    }
}



@Composable
fun AlarmScreen(navController: NavHostController) {
    val context = LocalContext.current
    var alarmTime by remember { mutableStateOf("00:00") } // State for the alarm time
    var showAlarm by remember { mutableStateOf(false) } // State to control if the alarm should be shown
    var errorMessage by remember { mutableStateOf("") } // State for error message

    // Function to validate and format the alarm time input
    fun isValidTime(time: String): Boolean {
        val parts = time.split(":")
        if (parts.size == 2) {
            val hours = parts[0].toIntOrNull()
            val minutes = parts[1].toIntOrNull()
            return hours != null && minutes != null && hours in 0..23 && minutes in 0..59
        }
        return false
    }

    // Extract hour and minute from the alarm time string, with validation
    val (alarmHour, alarmMinute) = if (isValidTime(alarmTime)) {
        alarmTime.split(":").let {
            it[0].toInt() to it[1].toInt()
        }
    } else {
        0 to 0 // Default to 00:00 if invalid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Alarm Time", fontSize = 24.sp, color = Color.White) // Change this to white for contrast

        // Input field to set the alarm time
        TextField(
            value = alarmTime,
            onValueChange = {
                if (isValidTime(it)) {
                    alarmTime = it
                    errorMessage = "" // Clear error message on valid input
                } else {
                    errorMessage = "Invalid time format! Please use HH:MM."
                }
            },
            label = { Text("Enter Time (HH:MM)", color = Color.White) }, // Label in white
            modifier = Modifier.padding(vertical = 8.dp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface) // Use surface background
        )

        // Display error message if invalid input
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        // Button to set the alarm
        Button(
            onClick = {
                if (isValidTime(alarmTime)) {
                    Toast.makeText(context, "Alarm set for $alarmTime", Toast.LENGTH_SHORT).show()
                    showAlarm = true // Set showAlarm to true to show AlarmFunction
                } else {
                    errorMessage = "Invalid time format! Please use HH:MM."
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary) // Use dynamic primary color
        ) {
            Text("Set Alarm", color = MaterialTheme.colors.onPrimary) // Text color based on the theme
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to go back to ToolsPage
        Button(
            onClick = {
                navController.popBackStack() // Navigates back to the previous screen (ToolsPage)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary) // Use secondary color
        ) {
            Text("Back to Tools", color = MaterialTheme.colors.onSecondary) // Text color based on the theme
        }

        // Conditionally show the AlarmFunction composable
        if (showAlarm) {
            AlarmFunction(context, alarmHour, alarmMinute) // Pass hour and minute to AlarmFunction
        }
    }
}