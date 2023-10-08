package com.sxu.imageoptimizer

import com.google.gson.GsonBuilder
import com.sxu.imageoptimizer.Utils.log
import com.sxu.imageoptimizer.Utils.matchedFileFormat
import com.tinify.Tinify
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/*******************************************************************************
 * The implementation process of image compression.
 *
 * @author: Freeman
 *
 * @date: 2023/4/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
abstract class OptimizerTask : DefaultTask() {

    /**
     * The image paths to be optimized
     */
    private val allImagePathList = mutableSetOf<String>()

    init {
        group = "plugin"
    }

    @TaskAction
    fun run() {
        // Get the optimizer configuration
        val config = project.extensions.getByName(taskConfigName) as? OptimizerConfig?
        if (config == null) {
            log("Please configure the TinyCompress task")
            return
        }
        // Check API key
        if (config.apiKey.isNullOrEmpty()) {
            log("API Key not configured")
            return
        }
        // Validate configuration
        if (!config.isAppendMode && config.resourceDirs.isNullOrEmpty()) {
            log("Configuration error: resourceDirs cannot be empty when isAppendMode is false")
            return
        }
        // Check if the API key is valid
        if (!checkApiKey(config.apiKey)) {
            log("Invalid TinyPng API Key")
            return
        }

        // Retrieve the list of directories to be optimized
        val waitOptimizerDirs = mutableListOf<String>()

        if (config.isAppendMode) {
            findAllImageDirs(config.supportFormat)
            waitOptimizerDirs.addAll(allImagePathList.toList())
            waitOptimizerDirs.addAll(config.resourceDirs)
        } else {
            waitOptimizerDirs.addAll(config.resourceDirs)
        }
        log(
            "Directories waiting for optimization: ${
                GsonBuilder().setPrettyPrinting().create().toJson(waitOptimizerDirs)
            }"
        )

        convertImage(waitOptimizerDirs, config)
        compressImage(waitOptimizerDirs, config)
        log("Task finished!")
    }

    /**
     * Check if the TinyPng API Key is valid
     * @param apiKey: TinyPng API Key
     */
    private fun checkApiKey(apiKey: String): Boolean {
        try {
            Tinify.setKey("$apiKey")
            Tinify.validate()
            return true
        } catch (e: Exception) {
            log(e.message ?: "")
        }
        return false
    }

    /**
     * Find all image directories
     * @param format: the image file format of found
     */
    private fun findAllImageDirs(format: Int) {
        project.rootProject.allprojects.forEach {
            // Find image paths in each module's 'assets' folder
            File("${project.rootDir.path}/${it.name}/src/main/assets").apply {
                if (exists()) {
                    findImagePath(this, format)
                }
            }
            // Find image paths in each module's 'src' folder
            File("${project.rootDir.path}/${it.name}/src/main/res").apply {
                if (exists()) {
                    findImagePath(this, format)
                }
            }
        }
    }

    /** Finds directories containing image files under the specified path.
     * @param directory: directory to search in.
     * @param format: the image file format of found
     */
    private fun findImagePath(directory: File, format: Int) {
        if (!directory.isDirectory) {
            return
        }
        directory.listFiles()?.apply {
            // Search in subdirectories.
            filter { it.isDirectory }.forEach { subDirectory ->
                findImagePath(subDirectory, format)
            }
            // Check if current directory contains specific format image files.
            val imageFile = filter { !it.isDirectory }.find { it.matchedFileFormat(format) }
            imageFile?.parent?.let { allImagePathList.add(it) }
        }
    }

    /**
     * Converts images in specified directories to target format
     * @param targetDirectories: Directories to convert images from
     * @param config: the configuration options to use for converting
     */
    protected open fun convertImage(targetDirectories: List<String>, config: OptimizerConfig) {

    }

    /**
     * Compress images in the target directory using the given configuration and list of already
     * optimized images.Only images that are not in the white list, not already optimized, and
     * larger than the skip size can be optimized.
     * @param targetDirectories the directory containing the images to compress
     * @param config the configuration options to use for compression
     */
    protected open fun compressImage(targetDirectories: List<String>, config: OptimizerConfig) {

    }
}