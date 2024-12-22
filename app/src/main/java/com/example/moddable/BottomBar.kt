package com.example.moddable

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

class BottomBar {
    @Composable
    fun Create(navController: NavHostController) {
        NavigationBar(
            containerColor = Color(0xFF212121),
            contentColor = Color.White
        ) {
            // Main Menu
            NavigationBarItem(
                selected = navController.currentBackStackEntry?.destination?.route == "main_menu",
                onClick = { navController.navigate("main_menu") },
                label = { Text("Main Menu") },
                icon = {
                    IconWithBackground(
                        icon = Icons.Default.Home,
                        label = "Main Menu",
                        isSelected = navController.currentBackStackEntry?.destination?.route == "main_menu"
                    )
                }
            )

            // Tools
            NavigationBarItem(
                selected = navController.currentBackStackEntry?.destination?.route == "tools",
                onClick = { navController.navigate("tools") },
                label = { Text("Tools") },
                icon = {
                    IconWithBackground(
                        icon = Icons.Default.Build,
                        label = "Tools",
                        isSelected = navController.currentBackStackEntry?.destination?.route == "tools"
                    )
                }
            )

            // Settings
            NavigationBarItem(
                selected = navController.currentBackStackEntry?.destination?.route == "settings",
                onClick = { navController.navigate("settings") },
                label = { Text("Settings") },
                icon = {
                    IconWithBackground(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isSelected = navController.currentBackStackEntry?.destination?.route == "settings"
                    )
                }
            )
        }
    }

    @Composable
    fun IconWithBackground(icon: ImageVector, label: String, isSelected: Boolean) {
        val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray

        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(48.dp)  // Set the size of the icon
                .clip(CircleShape)  // Clip the icon into a circle
                .background(backgroundColor)  // Change the background color based on selection
                .padding(12.dp),  // Padding around the icon for better spacing
            tint = Color.White // Ensure the icon is white
        )
    }
}
