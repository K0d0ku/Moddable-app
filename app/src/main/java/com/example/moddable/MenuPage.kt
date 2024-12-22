package com.example.moddable
    /* TODO - to add the shortcuts to the list of mods from toolspage */

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.Add
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class WebPage(
    val route: String,
    val name: String,
    val url: String
)

class MenuPage {

    // Define the list of dynamic web pages
    private val webPages = listOf(
        WebPage(route = "Reviews", name = "Reviews", url = "https://docs.google.com/forms/d/19z9ZGm9W-gZTMIoDlDEbvkn9p3ksLzEAOoTScsutFcw/viewanalytics"),
        WebPage(route = "Announcements", name = "Announcements", url = "https://github.com/K0d0ku/Moddable-app/discussions/categories/announcements?discussions_q=category%3AAnnouncements+")
        // Add more pages as needed
    )

    @Composable
    fun Content(navController: androidx.navigation.NavController) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Mood, // Correct usage
                    contentDescription = "",
                    modifier = Modifier
                        .size(148.dp) // Adjust the size of the icon
                        .padding(start = 8.dp) // Add spacing between the text and the icon
                )
                Text(
                    text = "Hello, User!",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )

                // Dynamically generate buttons for web pages
                webPages.forEach { page ->
                    Button(
                        onClick = { navController.navigate(page.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = page.name,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    fun getWebPages(): List<WebPage> = webPages
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    val isLoading = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading.value = false
                        }
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
