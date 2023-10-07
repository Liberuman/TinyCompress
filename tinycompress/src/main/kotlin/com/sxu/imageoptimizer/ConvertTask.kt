package com.sxu.imageoptimizer

import com.sxu.imageoptimizer.Utils.log
import com.sxu.imageoptimizer.Utils.matchedFileFormat
import com.tinify.Options
import com.tinify.Tinify
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
open class ConvertTask : OptimizerTask() {

    /**
     * Converts images in specified directories to target format
     * @param targetDirectories: Directories to convert images from
     * @param config: the configuration options to use for converting
     */
     override fun convertImage(
        targetDirectories: List<String>,
        config: OptimizerConfig
    ) {
        for (directory in targetDirectories) {
            val file = File(directory)
            if (!file.exists() || !file.isDirectory) {
                continue
            }
            file.listFiles().asSequence()
                .filter {
                    it.matchedFileFormat(config.supportFormat) &&
                            !it.name.lowercase().endsWith(".${config.targetFormat}") &&
                            config.convertWhiteList?.contains(it.name) != true
                }
                .forEach {
                    try {
                        val result = Tinify.fromFile(it.path)
                            .convert(Options().with("type", arrayOf("image/${config.targetFormat}")))
                            .result()
                        result.toFile("${it.parent}/${it.nameWithoutExtension}.${result.extension()}")
                        it.delete()
                    } catch (e: Exception) {
                        log(e.message ?: "An exception occurred when converting to webp")
                    }
                }
        }
    }
}