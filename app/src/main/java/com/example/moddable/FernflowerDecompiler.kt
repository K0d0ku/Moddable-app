package com.example.moddable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.zip.ZipFile

/*this is never used*/

class FernflowerDecompiler(
    private val context: Context,
    private val jarInputFile: File
) {
    private val baseOutputDir: File = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
        "ModdableMain/Mods"
    )

    companion object {
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    init {
        if (!hasRequiredPermissions()) {
            throw SecurityException("Required storage permissions are not granted")
        }

        if (!jarInputFile.exists() || !jarInputFile.isFile) {
            throw FileNotFoundException("JAR input file not found: ${jarInputFile.absolutePath}")
        }

        if (!baseOutputDir.exists()) {
            if (!baseOutputDir.mkdirs()) {
                throw IOException("Failed to create base output directory")
            }
        }
    }

    /**
     * Checks if all required permissions are granted
     */
    private fun hasRequiredPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Checks if the app has all storage permissions and proper storage access
     */
    fun canAccessStorage(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager() || hasRequiredPermissions()
        } else {
            hasRequiredPermissions()
        }
    }

    /**
     * Decompiles the input JAR file using Fernflower.
     * The output is a directory containing decompiled Java source files.
     * @throws SecurityException if required permissions are not granted
     */
    fun decompile() {
        if (!canAccessStorage()) {
            throw SecurityException("Storage access not granted")
        }

        cleanMemory()
        try {
            val jarName = jarInputFile.nameWithoutExtension
            val jarOutputDir = File(baseOutputDir, jarName)

            if (!jarOutputDir.exists() && !jarOutputDir.mkdirs()) {
                throw IOException("Failed to create output directory for JAR: ${jarOutputDir.absolutePath}")
            }

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

    /**
     * Extracts the decompiled JAR content into the output directory.
     */
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

    /**
     * Frees up memory to ensure smooth execution.
     */
    private fun cleanMemory() {
        System.gc()
    }
}