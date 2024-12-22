package com.example.moddable

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moddable.ui.theme.ModdableTheme
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the file picker launcher
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Log the URI
                Log.d("MainActivity", "URI selected: $uri")
                val file = File(uri.path ?: "")
                Log.d("MainActivity", "File path: ${file.absolutePath}")

                // Show a toast with the file name
                Toast.makeText(this, "Mod selected: ${file.name}", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("MainActivity", "No file selected or URI is null")
            }
        }

        // Set up your Compose content here
        setContent {
            ModdableTheme {
                // Pass both filePickerLauncher and the applicationContext
                MainScreen(filePickerLauncher = filePickerLauncher, context = this)
            }
        }
    }
}

@Composable
fun MainScreen(filePickerLauncher: ActivityResultLauncher<String>, context: Context) {
    val navController = rememberNavController()
    val menuPage = MenuPage()

    Scaffold(
        bottomBar = {
            val bottomBar = BottomBar()
            bottomBar.Create(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main_menu",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main_menu") {
                menuPage.Content(navController)
            }
            composable("tools") {
                val toolsPage = ToolsPage(toolsContext = context, filePickerLauncher = filePickerLauncher)
                toolsPage.Content(navController)
            }
            composable("settings") {
                val settingsPage = SettingsPage()
                settingsPage.Content()
            }

            composable("alarm_page") {
                AlarmScreen(navController) // Pass navController here
            }

            composable("pointer_tracker") {
                PointerTracker(navController = navController) // This should be the new UI for pointer tracking
            }

            // Dynamically generate routes for web pages
            menuPage.getWebPages().forEach { page ->
                composable(page.route) {
                    WebViewScreen(page.url)
                }
            }
        }
    }
}