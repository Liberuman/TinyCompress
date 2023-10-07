package com.sxu.imageoptimizer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sxu.imageoptimizer.Utils.formatSize
import com.sxu.imageoptimizer.Utils.getFileMd5
import com.sxu.imageoptimizer.Utils.log
import com.sxu.imageoptimizer.Utils.matchedFileFormat
import com.tinify.AccountException
import com.tinify.Tinify
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

/*******************************************************************************
 * The implementation process of image compression.
 *
 * @author: Freeman
 *
 * @date: 2023/4/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
open class CompressTask : OptimizerTask() {

    /**
     * Compress images in the target directory using the given configuration and list of already
     * optimized images.Only images that are not in the white list, not already optimized, and
     * larger than the skip size can be optimized.
     * @param targetDirectories the directory containing the images to compress
     * @param config the configuration options to use for compression
     */
    override fun compressImage(targetDirectories: List<String>, config: OptimizerConfig) {
        super.compressImage(targetDirectories, config)
        // Retrieve the previously optimized image information
        val optimizedListFile = File("${project.rootDir}/compressedList.json")
        val optimizedList = if (optimizedListFile.exists()) {
            try {
                val reader = InputStreamReader(optimizedListFile.inputStream())
                Gson().fromJson<List<ImageInfo>>(
                    reader,
                    object : TypeToken<List<ImageInfo>>() {}.type
                ) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace(System.err)
                emptyList()
            }
        } else {
            emptyList()
        }
        // Calculate the total size of images before and after optimization
        var beforeSize = 0L
        var afterSize = 0L
        log("Start compression...")
        var overLimit = false
        val newOptimizedList = mutableListOf<ImageInfo>()
        for (dir in targetDirectories) {
            val file = File(dir)
            if (!file.exists() || !file.isDirectory) {
                continue
            }
            val optimizeResult = startCompress(file, config, optimizedList)
            // If the free usage limit has been exceeded, exit the loop
            if (optimizeResult.overLimit) {
                overLimit = true
                break
            }
            // If no images meet the criteria, move on to the next directory
            if (optimizeResult.optimizedList.isNullOrEmpty()) {
                continue
            }
            beforeSize += optimizeResult.beforeSize
            afterSize += optimizeResult.afterSize
            newOptimizedList.addAll(optimizeResult.optimizedList)
        }
        if (newOptimizedList.isEmpty()) {
            if (!overLimit) {
                log("No images need to be compressed!!!")
            }
            return
        }
        // Add the newly optimized images to the list of optimized images
        val finalOptimizedList = optimizedList + newOptimizedList
        if (!optimizedListFile.exists()) {
            optimizedListFile.createNewFile()
        }
        optimizedListFile.writeText(
            GsonBuilder().setPrettyPrinting().create().toJson(finalOptimizedList),
            Charset.forName("utf-8")
        )
        if (afterSize > 0L) {
            log(
                "Optimized ${newOptimizedList.filter { !it.ignore }.size} images, before total size: ${
                    beforeSize.formatSize()
                }, after total size: ${afterSize.formatSize()}, decreased ${(beforeSize - afterSize).formatSize()}"
            )
        } else {
            log("No images need to be compressed!!!")
        }
    }

    /**
     * Optimizes images in the target directory using the given configuration and list of already
     * optimized images.Only images that are not in the white list, not already optimized, and
     * larger than the skip size can be optimized.
     * @param targetDirectory the directory containing the images to compress
     * @param config the configuration options to use for compression
     * @param optimizedList the list of already compressed images
     * @return an OptimizeResult object containing information about the compression process
     */
    private fun startCompress(
        targetDirectory: File,
        config: OptimizerConfig,
        optimizedList: List<ImageInfo>?
    ): OptimizeResult {
        val result = OptimizeResult()
        // Only process image files that meet the conditions for optimization
        targetDirectory.listFiles()
            .asSequence()
            .filter {
                it.matchedFileFormat(config.supportFormat)
                        && it.length() > config.skipSize
                        && config.whiteList?.contains(it.name) != true
                        && optimizedList?.find { it1 -> it1.path == it.path }.run {
                    this == null || md5 != it.getFileMd5()
                }
            }.apply {
                for (file in this) {
                    val beforeSize = file.length()
                    try {
                        // Optimize the image using the Tinify API
                        val optimizedFile = Tinify.fromFile(file.path).result()
                        val compressRatio =
                            (beforeSize - optimizedFile.size()) * 100.0f / beforeSize
                        // Add information about the optimized image to the result object
                        val imageInfo = ImageInfo(
                            path = file.path,
                            beforeSize = beforeSize,
                            afterSize = optimizedFile.size().toLong(),
                            md5 = file.getFileMd5()
                        )
                        // When the compression rate of an image is lower than the specified
                        // threshold, the compression result is ignored.
                        if (compressRatio >= 0 && compressRatio < config.compressRatioThreshold) {
                            imageInfo.ignore = true
                            result.optimizedList.add(imageInfo)
                            continue
                        }

                        log("Find target picture -> ${file.path}")
                        result.optimizedList.add(imageInfo)
                        // Write the optimized image to disk
                        optimizedFile.toFile("${file.path}")
                        result.beforeSize += imageInfo.beforeSize
                        result.afterSize += optimizedFile.size()
                    } catch (e: AccountException) {
                        // Handle errors related to the Tinify API account
                        result.overLimit = true
                        log(e.message ?: "An exception occurred")
                        break
                    } catch (e: Exception) {
                        // Handle other types of exceptions
                        log(e.message ?: "An exception occurred")
                        break
                    }
                }
            }

        return result
    }



}