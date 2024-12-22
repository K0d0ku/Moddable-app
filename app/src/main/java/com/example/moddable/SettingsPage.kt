package com.example.moddable

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.Add
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.compose.rememberLauncherForActivityResult
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
import java.io.File
import java.io.IOException
import java.util.zip.ZipFile

class SettingsPage {

    @Composable
    fun Content() {
        var selectedCategory by remember { mutableStateOf<String?>(null) }

        if (selectedCategory == null) {
            InitialGrid(onCategorySelected = { selectedCategory = it })
        } else {
            SidebarLayout(
                selectedCategory = selectedCategory!!,
                onCategorySelected = { selectedCategory = it },
                onBackToMainGrid = { selectedCategory = null }
            )
        }
    }

    @Composable
    private fun InitialGrid(onCategorySelected: (String) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Settings Categories", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { CategoryIcon("Mods", onCategorySelected) }
                item { CategoryIcon("Appearance", onCategorySelected) }
                item { CategoryIcon("Interaction", onCategorySelected) }
                item { CategoryIcon("Tutorials", onCategorySelected) }
            }
        }
    }

    @Composable
    private fun CategoryIcon(label: String, onCategorySelected: (String) -> Unit) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
                .clickable { onCategorySelected(label) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val icon = when (label) {
                "Mods" -> Icons.Filled.Build
                "Appearance" -> Icons.Filled.Settings
                "Interaction" -> Icons.Filled.Interests
                "Tutorials" -> Icons.Filled.Info
                else -> Icons.Filled.Help
            }

            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                tint = Color.White
            )

            Text(
                label,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    @Composable
    private fun SidebarLayout(
        selectedCategory: String,
        onCategorySelected: (String) -> Unit,
        onBackToMainGrid: () -> Unit
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar with 30% width
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val categories = listOf("Mods", "Appearance", "Interaction", "Tutorials")

                // Category Icons as Buttons
                categories.forEach { category ->
                    SidebarCategoryIcon(
                        label = category,
                        isSelected = selectedCategory == category,
                        onClick = { onCategorySelected(category) }
                    )
                }

                // Back to Categories Icon Button at the bottom
                IconButton(
                    onClick = onBackToMainGrid,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowCircleLeft,  // Back icon
                        contentDescription = "Back to Categories"
                    )
                }
            }
            // Content Area on the right side
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                CategoryContentGrid(category = selectedCategory)
            }
        }
    }

    @Composable
    private fun SidebarCategoryIcon(
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val icon = when (label) {
                "Mods" -> Icons.Filled.Build
                "Appearance" -> Icons.Filled.Settings
                "Interaction" -> Icons.Filled.Interests
                "Tutorials" -> Icons.Filled.Info
                else -> Icons.Filled.Help
            }

            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray),
                tint = Color.White
            )

            Text(
                text = label,
                fontSize = 7.sp,
                modifier = Modifier
//                    .padding(top = 2.dp)
                    .align(Alignment.CenterHorizontally),
                maxLines = 1
            )
        }
    }

    @Composable
    private fun CategoryContentGrid(category: String) {
        val dlurl = LocalContext.current
        val modderjoin = LocalContext.current
        val review = LocalContext.current
        val reviewGit = LocalContext.current
        val discussionGit = LocalContext.current
        val maindolder = LocalContext.current
        val installmods = LocalContext.current
        val delmods = LocalContext.current
        var showInstallMods by remember { mutableStateOf(false) }
        var folderCreationStatus by remember { mutableStateOf("") }
        var showDeleteModsDialog by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "$category Options",
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (category) {
                    "Mods" -> {
                        item { OptionIcon("Install Mods", Icons.Filled.InstallMobile) { showInstallMods = true } }
                        item { OptionIcon("Download Mods", Icons.Filled.Download) { downloadMods(dlurl) } }
                        item { OptionIcon("Delete Mods", Icons.Filled.Delete) { showDeleteModsDialog = true } }

                        item {
                            OptionIcon(
                                "Check/Create Main Folders",
                                Icons.Filled.CreateNewFolder
                            ) {
                                checkAndCreateModsFolder(maindolder, onFolderCreationStatus = { status ->
                                    folderCreationStatus = status // Update the status text
                                })
                            }
                        }
                        item {
                            Text(
                                text = "Folder Creation Status: $folderCreationStatus",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                    }
                    "Appearance" -> {
                        item { OptionIcon("Themes", Icons.Filled.ImagesearchRoller) { openThemes() } }
                        item { OptionIcon("Fonts", Icons.Filled.TextFields) { changeFonts() } }
                        item { OptionIcon("Presets", Icons.Filled.FormatLineSpacing) { setPresets() } }
                    }
                    "Interaction" -> {
                        item { OptionIcon("Leave a Review", Icons.Filled.Reviews) { leaveReview(review) } }
                        item { OptionIcon("Become a Modder", Icons.Filled.GroupAdd) { becomeModder(modderjoin) } }
                        item { OptionIcon("Discussions (GitHub)", Icons.Filled.Chat) { discussionfunc(discussionGit) } }
                        item { OptionIcon("Review (GitHub)", Icons.Filled.RateReview) { leaveReviewGit(reviewGit) } }
                    }
                    "Tutorials" -> {
                        item { OptionIcon("How to load Mods", Icons.Filled.QuestionMark) { modsTutorial() } }
                    }
                    else -> {
                        item { OptionIcon("Unknown Option", Icons.Filled.Help) { showUnknown() } }
                    }
                }
            }

            if (folderCreationStatus.isNotEmpty()) {
                Text(
                    text = folderCreationStatus,
                    color = Color.Green, // You can change the color if needed
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (showInstallMods) {
                InstallModsComposable(onDismiss = { showInstallMods = false }, context = maindolder)
            }

            if (showDeleteModsDialog) {
                DeleteModsDialog(
                    onDismiss = { showDeleteModsDialog = false },
                    context = delmods
                )
            }

        }
    }

    private fun checkAndCreateModsFolder(maindolder: Context, onFolderCreationStatus: (String) -> Unit) {
        // Get the Documents directory path
        val documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val moddableMainFolder = File(documentsDirectory, "ModdableMain")

        // Check if the ModdableMain folder exists, if not, create it and all subfolders
        if (!moddableMainFolder.exists()) {
            val folderCreated = moddableMainFolder.mkdirs()
            if (folderCreated) {
                // Create subfolders inside ModdableMain
                val modsFolder = File(moddableMainFolder, "Mods")
                val appearanceFolder = File(moddableMainFolder, "Appearance")
                val securityFolder = File(moddableMainFolder, "Sequrity")

                val presetsFolder = File(appearanceFolder, "Presets")
                val themesFolder = File(appearanceFolder, "Themes")
                val soundsFolder = File(appearanceFolder, "Sounds")

                val passwordsFolder = File(securityFolder, "Passwords")

                // Create all the subfolders
                modsFolder.mkdirs()
                appearanceFolder.mkdirs()
                securityFolder.mkdirs()
                presetsFolder.mkdirs()
                themesFolder.mkdirs()
                soundsFolder.mkdirs()
                passwordsFolder.mkdirs()

                // Notify the user about the folder creation
                onFolderCreationStatus("ModdableMain folder and subfolders created!")
            } else {
                onFolderCreationStatus("Failed to create ModdableMain folder!")
            }
        } else {
            // If ModdableMain exists, check each folder
            val modsFolder = File(moddableMainFolder, "Mods")
            val appearanceFolder = File(moddableMainFolder, "Appearance")
            val securityFolder = File(moddableMainFolder, "Sequrity")

            val presetsFolder = File(appearanceFolder, "Presets")
            val themesFolder = File(appearanceFolder, "Themes")
            val soundsFolder = File(appearanceFolder, "Sounds")

            val passwordsFolder = File(securityFolder, "Passwords")

            // Check each folder and create if not exists
            val folderStatus = mutableListOf<String>()

            if (!modsFolder.exists()) {
                modsFolder.mkdirs()
                folderStatus.add("Mods folder created!")
            } else {
                folderStatus.add("Mods folder already exists.")
            }

            if (!appearanceFolder.exists()) {
                appearanceFolder.mkdirs()
                folderStatus.add("Appearance folder created!")
            } else {
                folderStatus.add("Appearance folder already exists.")
            }

            if (!securityFolder.exists()) {
                securityFolder.mkdirs()
                folderStatus.add("Security folder created!")
            } else {
                folderStatus.add("Security folder already exists.")
            }

            if (!presetsFolder.exists()) {
                presetsFolder.mkdirs()
                folderStatus.add("Presets folder created!")
            } else {
                folderStatus.add("Presets folder already exists.")
            }

            if (!themesFolder.exists()) {
                themesFolder.mkdirs()
                folderStatus.add("Themes folder created!")
            } else {
                folderStatus.add("Themes folder already exists.")
            }

            if (!soundsFolder.exists()) {
                soundsFolder.mkdirs()
                folderStatus.add("Sounds folder created!")
            } else {
                folderStatus.add("Sounds folder already exists.")
            }

            if (!passwordsFolder.exists()) {
                passwordsFolder.mkdirs()
                folderStatus.add("Passwords folder created!")
            } else {
                folderStatus.add("Passwords folder already exists.")
            }

            // Combine all the folder statuses into a message
            val statusMessage = folderStatus.joinToString("\n")
            onFolderCreationStatus(statusMessage)
        }
    }

    // OptionIcon Composable that takes a clickAction lambda
    @Composable
    private fun OptionIcon(label: String, icon: ImageVector, clickAction: () -> Unit) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.8f)
                .clickable { clickAction() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Text(
                label,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                maxLines = 2,
                overflow = TextOverflow.Visible
            )
        }
    }

//    placeholder or kotlin mover directly
    @Composable
    fun InstallModsComposable(onDismiss: () -> Unit, context: Context) {
        var hasStoragePermissions by remember { mutableStateOf(checkStoragePermissions(context)) }

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            hasStoragePermissions = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    Environment.isExternalStorageManager()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                }
                else -> {
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                }
            }
        }

        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                handleSelectedFile(context, uri)
            }
        }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Install Mods") },
            text = {
                if (!hasStoragePermissions) {
                    Text("Storage permissions are required to install mods. Please grant the necessary permissions.")
                } else {
                    Text("Select a mod file to install.")
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (hasStoragePermissions) {
                        filePickerLauncher.launch(arrayOf("application/octet-stream"))
                    } else {
                        requestStoragePermissions(context, permissionLauncher)
                    }
                }) {
                    Text(if (hasStoragePermissions) "Choose File" else "Grant Permissions")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }

    private fun checkStoragePermissions(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // For Android 11 (API level 30) and above, checking all files access
                Environment.isExternalStorageManager()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // For Android 10 (API level 29), checking if we have read access
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // For Android 9 (API level 28) and below, we need both read and write permissions
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun requestStoragePermissions(
        context: Context,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Request "All Files Access" for Android 11+ devices
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        addCategory("android.intent.category.DEFAULT")
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    context.startActivity(intent)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Request read permissions for Android 10+
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ))
            }
            else -> {
                // Request both read and write permissions for Android 9 and below
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
        }
    }

    private fun handleSelectedFile(context: Context, uri: Uri) {
        val filePath = uriToFilePath(context, uri)
        if (filePath != null) {
            val destinationFile = moveFileToModsDirectory(context, filePath)
            if (destinationFile != null) {
                Toast.makeText(
                    context,
                    "Mod installed at: ${destinationFile.absolutePath}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Failed to move the file.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "Failed to get file path.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uriToFilePath(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            val fileName = it.getString(nameIndex)
            val tempFile = File(context.cacheDir, fileName)  // Using cache directory temporarily
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile.absolutePath
        }
    }

    private fun moveFileToModsDirectory(context: Context, sourcePath: String): File? {
        // Target directory in the user's Documents
        val documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val moddableMainFolder = File(documentsDirectory, "ModdableMain")
        val modsFolder = File(moddableMainFolder, "Mods")

        // Ensure the target directory exists
        if (!modsFolder.exists()) {
            if (!modsFolder.mkdirs()) {
                return null  // Failed to create Mods directory
            }
        }

        // Move the file to the Mods folder
        val sourceFile = File(sourcePath)
        val destinationFile = File(modsFolder, sourceFile.name)

        return try {
            sourceFile.copyTo(destinationFile, overwrite = true) // Copies the file to the destination
            sourceFile.delete() // Optionally delete the source file after copying
            destinationFile
        } catch (e: Exception) {
            e.printStackTrace()
            null  // Return null if there's an error
        }
    }

//    needs to change to vinefloewer or fernflower in kotlin
    /*@Composable
    fun InstallModsComposable(onDismiss: () -> Unit, context: Context) {
        var hasStoragePermissions by remember { mutableStateOf(checkStoragePermissions(context)) }

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            hasStoragePermissions = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    Environment.isExternalStorageManager()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                }
                else -> {
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                }
            }
        }

        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                Log.d("FilePicker", "Selected URI: $uri")
                val jarFilePath = uriToFilePath(context, uri) // Convert URI to file path
                Log.d("FilePicker", "Jar File Path: $jarFilePath")

                if (jarFilePath != null && jarFilePath.endsWith(".jar")) {
                    // Show Toast message when the file is selected
                    Toast.makeText(context, "JAR file selected: $jarFilePath", Toast.LENGTH_SHORT).show()

                    try {
                        val jarFile = File(jarFilePath)
                        // Start decompilation and show Toast when it begins
                        Toast.makeText(context, "Starting decompilation...", Toast.LENGTH_SHORT).show()

                        decompileJar(context, jarFile) // Handle decompilation here

                        val outputDir = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                            "ModdableMain/Mods/${jarFile.nameWithoutExtension}"
                        )

                        Toast.makeText(context, "Decompilation successful: ${outputDir.absolutePath}", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Decompilation failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Selected file is not a valid JAR file.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No file selected.", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Install Mods") },
            text = {
                if (!hasStoragePermissions) {
                    Text("Storage permissions are required to install mods. Please grant the necessary permissions.")
                } else {
                    Text("Select a mod file to install.")
                    Log.d("selection", "selection")
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (hasStoragePermissions) {
                        filePickerLauncher.launch(arrayOf("application/java-archive")) // Trigger the file picker
                        onDismiss()
                    } else {
                        requestStoragePermissions(context, permissionLauncher) // Request permissions
                    }
                }) {
                    Text(if (hasStoragePermissions) "Choose File" else "Grant Permissions")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }

    private fun decompileJar(context: Context, jarInputFile: File) {
        val baseOutputDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "ModdableMain/Mods"
        )

        // Check permissions
        if (!hasRequiredPermissions(context)) {
            throw SecurityException("Required storage permissions are not granted")
        }

        // Create output directory if not exists
        if (!baseOutputDir.exists() && !baseOutputDir.mkdirs()) {
            throw IOException("Failed to create base output directory")
        }

        // Perform decompilation using Fernflower
        val jarName = jarInputFile.nameWithoutExtension
        val jarOutputDir = File(baseOutputDir, jarName)
        Log.d("Decompile", "Decompiling JAR: ${jarInputFile.name}")

        if (!jarOutputDir.exists() && !jarOutputDir.mkdirs()) {
            throw IOException("Failed to create output directory for JAR: ${jarOutputDir.absolutePath}")
        }

        // Show Toast when decompilation starts
        Toast.makeText(context, "Decompiling JAR: ${jarInputFile.name}", Toast.LENGTH_SHORT).show()

        try {
            ConsoleDecompiler.main(
                arrayOf(
                    jarInputFile.canonicalPath,
                    jarOutputDir.canonicalPath
                )
            )
            extractDecompiledJar(jarOutputDir)
        } catch (e: Exception) {
            throw RuntimeException("Decompilation failed: ${e.message}", e)
        }
    }

    private fun extractDecompiledJar(outputDir: File) {
        outputDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "jar") {
                ZipFile(file).use { zip ->
                    zip.entries().asSequence().forEach { entry ->
                        val outputFile = File(outputDir, entry.name)
                        if (entry.isDirectory) {
                            if (!outputFile.mkdirs() && !outputFile.exists()) {
                                throw IOException("Failed to create directory: ${outputFile.absolutePath}")
                            }
                        } else {
                            outputFile.parentFile?.let {
                                if (!it.exists() && !it.mkdirs()) {
                                    throw IOException("Failed to create parent directory: ${it.absolutePath}")
                                }
                            }
                            zip.getInputStream(entry).use { input ->
                                outputFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }
                    }
                }
                file.delete() // Remove the decompiled JAR after extraction
            }
        }
    }

    private fun hasRequiredPermissions(context: Context): Boolean {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun uriToFilePath(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            val fileName = it.getString(columnIndex)

            val tempFile = File(context.cacheDir, fileName) // Use cacheDir to temporarily store the file
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile.absolutePath // Return the absolute path of the temporary file
        }
    }

    private fun checkStoragePermissions(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Environment.isExternalStorageManager()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun requestStoragePermissions(
        context: Context,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        addCategory("android.intent.category.DEFAULT")
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    context.startActivity(intent)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ))
            }
            else -> {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
        }
    }*/

    private fun downloadMods(dlurl: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/K0d0ku/Moddable-app/forks"))
        dlurl.startActivity(intent)
    }



    @Composable
    fun DeleteModsDialog(onDismiss: () -> Unit, context: Context) {
        var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Toast.makeText(context, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

        val multipleFilesLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->
            if (uris.isNotEmpty()) {
                selectedFiles = uris
            }
        }

        // Check permissions when dialog opens
        LaunchedEffect(Unit) {
            checkAndRequestPermissions(context, permissionLauncher)
        }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Delete Mods") },
            text = {
                Column {
                    Text("Select mods to delete")
                    if (selectedFiles.isNotEmpty()) {
                        Text(
                            "Selected files: ${selectedFiles.size}",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(selectedFiles.size) { index ->
                                val fileName = getFileName(context, selectedFiles[index])
                                Text(
                                    text = fileName ?: "Unknown file",
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    Button(
                        onClick = {
                            multipleFilesLauncher.launch(arrayOf("*/*"))
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Choose Files")
                    }
                    Button(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) {
                                deleteSelectedFiles(context, selectedFiles)
                                onDismiss()
                            }
                        },
                        enabled = selectedFiles.isNotEmpty()
                    ) {
                        Text("Delete Selected")
                    }
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    private fun deleteSelectedFiles(context: Context, uris: List<Uri>) {
        uris.forEach { uri ->
            try {
                // Get the real file path from URI
                val documentFile = DocumentFile.fromSingleUri(context, uri)

                if (documentFile?.exists() == true) {
                    val fileName = documentFile.name

                    if (documentFile.delete()) {
                        // Successfully deleted
                        Toast.makeText(
                            context,
                            "Successfully deleted: $fileName",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Failed to delete
                        Toast.makeText(
                            context,
                            "Failed to delete: $fileName",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "File not found or inaccessible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error during deletion: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    // Add this function to check and request permissions if needed
    private fun checkAndRequestPermissions(context: Context, permissionLauncher: ActivityResultLauncher<Array<String>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${context.packageName}")
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    context.startActivity(intent)
                }
            }
        } else {
            // For Android 10 and below
            val readPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            val permissionsToRequest = mutableListOf<String>()

            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (permissionsToRequest.isNotEmpty()) {
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }


    private fun openThemes() { /* TODO Open Themes functionality */ }
    private fun changeFonts() { /* TODO Change Fonts functionality */ }
    private fun setPresets() { /* TODO Set Presets functionality */ }
    private fun leaveReview(review: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/T6669Cm5AzsZ4iGB8"))
        review.startActivity(intent)
    }
    private fun becomeModder(modderjoin: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/K0d0ku/Moddable-app"))
        modderjoin.startActivity(intent)
    }
    private fun discussionfunc(discussionGit: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/K0d0ku/Moddable-app/discussions"))
        discussionGit.startActivity(intent)
    }
    private fun leaveReviewGit(reviewGit: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/K0d0ku/Moddable-app/discussions/12"))
        reviewGit.startActivity(intent)
    }
    private fun modsTutorial() { /* Show Tutorial functionality */ }
    /*TODO - to create a tutorials on how to load the mods and use the ui settings*/
    private fun showUnknown() { println("unknown") }
}