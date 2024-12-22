package com.example.moddable
    /* TODO rewrite this to show the list of available mods that are decompiled*/

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dalvik.system.DexClassLoader
import java.io.File
import androidx.compose.foundation.clickable
import androidx.navigation.NavHostController

class ToolsPage(
    val toolsContext: Context,
    val filePickerLauncher: ActivityResultLauncher<String>
) {
    @Composable
    fun Content(navController: NavHostController) {
        val modFiles = getModFiles(toolsContext)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (modFiles.isEmpty()) {
                Text(text = "No mods found", fontSize = 24.sp)
            } else {
                ModList(
                    modFiles,
                    onModSelected = { selectedMod ->
                        if (selectedMod.name == "clockmod.kt") {
                            // Navigate to the alarm page when clockmod is selected
                            navController.navigate("alarm_page")
                        } else if (selectedMod.name == "pointertracker.kt") {
                            // Display Pointer Tracker tool when selected
                            navController.navigate("pointer_tracker")
                        } else {
                            executeMod(toolsContext, selectedMod)
                            Toast.makeText(
                                toolsContext,
                                "Executing mod: ${selectedMod.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

    // Function to get mod files from the directory
    fun getModFiles(toolsContext: Context): List<File> {
        val modsDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "ModdableMain/Mods"
        )
        if (modsDirectory.exists() && modsDirectory.isDirectory) {
            return modsDirectory.listFiles { _, name -> name.endsWith(".kt") }?.toList() ?: emptyList()
        }
        return emptyList()
    }

    // UI for displaying list of mod files
    @Composable
    fun ModList(modFiles: List<File>, onModSelected: (File) -> Unit) {
        Column {
            modFiles.forEach { modFile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onModSelected(modFile) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = modFile.name,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    // UI for running the alarm function
    @Composable
    fun AlarmUI(toolsContext: Context, onStop: () -> Unit) {
        val alarmTime = 7  // Represents the hour
        val alarmMinute = 30  // Represents the minute

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Alarm is running!", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                onStop() // Stop the alarm
                Toast.makeText(toolsContext, "Alarm stopped", Toast.LENGTH_SHORT).show()
            }) {
                Text("Stop Alarm")
            }

            // Pass both alarmTime (hour) and alarmMinute (minute)
            AlarmFunction(toolsContext, alarmTime, alarmMinute)
        }
    }

    // Execute mod file (e.g., for clockmod)
    fun executeMod(execMods: Context, modFile: File) {
        try {
            val jarFile = File(execMods.cacheDir, "tempMod.jar")  // Example: Create temporary JAR
            val dexClassLoader = DexClassLoader(
                jarFile.absolutePath,
                execMods.cacheDir.absolutePath,
                null,
                execMods.classLoader
            )

            // Load the class
            val modClass = dexClassLoader.loadClass("Main")  // The class name must be predefined in the mod

            // Create an instance of the class and invoke methods dynamically
            val modInstance = modClass.newInstance()
            val method = modClass.getDeclaredMethod("main")  // Example method name
            method.invoke(modInstance)

        } catch (e: Exception) {
            Toast.makeText(execMods, "Error executing mod: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}