package com.sxu.imageoptimizer

import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.text.DecimalFormat

/******************************************************************************
 * Description:
 *
 * Author: jhg
 *
 * Date: 2023/9/27
 *
 * Copyright: all rights reserved by Mantang.
 *******************************************************************************/
object Utils {

    /**
     * Calculates the MD5 hash of a file.
     */
    fun File.getFileMd5(): String {
        // Generate unique key using file path and last modified time
        val key = "${path}${lastModified()}"
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(key.toByteArray(Charset.forName("utf-8")))
        // Convert byte array to hexadecimal string and pad with zeroes if necessary
        return BigInteger(1, bytes).toString(16).padStart(32, '0')
    }

    /**
     * Checks whether the file matches the given format.
     * @return true if this file type matched; false otherwise.
     */
    fun File.matchedFileFormat(format: Int): Boolean {
        val extension = this.extension
        return when (format and SupportFormatEnum.SUPPORT_ALL.value) {
            SupportFormatEnum.SUPPORT_WEBP_ONLY.value -> extension.equals("webp", true)
            SupportFormatEnum.SUPPORT_PNG_ONLY.value -> extension.equals("png", true) && !this.nameWithoutExtension.contains(".9.")
            SupportFormatEnum.SUPPORT_JPEG_ONLY.value -> extension.equals(
                "jpeg",
                true
            ) || extension.equals("jpg", true)
            SupportFormatEnum.SUPPORT_ALL.value -> {
                when (extension) {
                    "webp", "jpeg", "jpg" -> true
                    "png" -> !this.nameWithoutExtension.contains(".9.")
                    else -> false
                }
            }
            else -> {
                false
            }
        }
    }

    /**
     * Formats file size into a user-readable string
     */
    fun Long.formatSize(): String {
        val df = DecimalFormat("#.00")
        if (this == 0L) {
            return "0B"
        }

        return if (this < 1024) {
            df.format(this) + "B"
        } else if (this < 1024 * 1024) {
            df.format(this.toDouble() / 1024) + "KB"
        } else if (this < 1024 * 1024 * 1024) {
            df.format(this.toDouble() / (1024 * 1024)) + "MB"
        } else {
            df.format(this.toDouble() / 1024 * 1024 * 1024) + "GB"
        }
    }

    fun log(msg: String) {
        println("TinyCompress: $msg")
    }
}