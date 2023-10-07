package com.sxu.imageoptimizer

/*******************************************************************************
 * The configuration of compress image task.
 *
 * @author: Freeman
 *
 * @date: 2023/4/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
open class OptimizerConfig(

    /**
     * TinyPng API Key used for image compression
     */
    var apiKey: String = "",

    /**
     *  Flag to indicate whether to convert all images to specific format, null indicate convert can't working
     */
    var targetFormat: String? = null,

    /**
     * A list of resource file or directory names that do not need to be convert.
     */
    var convertWhiteList: List<String>? = listOf(),

    /**
     * Size threshold for skipping compression of images in bytes. For example, images below 10K
     * do not need to be compressed and can be configured as 10 * 1024. The default is images
     * below 5K will not be compressed.
     */
    var skipSize: Int = 5 * 1024,

    /**
     * List of supported image formats for compression or converting
     */
    var supportFormat: Int = SupportFormatEnum.SUPPORT_ALL.value,

    /**
     * Compression ratio threshold. Only replace the original file after compression when the
     * compression ratio is higher than this value to avoid multiple compressions.
     * A value of 30% is recommended.
     */
    var compressRatioThreshold: Int = 30,

    /**
     *  Flag to indicate whether to append the resourceDirs path to the optimization list. If set to
     *  false, only optimize the image resources in the resourceDirs path.
     */
    var isAppendMode: Boolean = true,

    /**
     * A list of resource file or directory names that do not need to be compressed.
     */
    var whiteList: List<String>? = listOf(),

    /**
     * A list of paths of resources that need to be compressed. By default, all images in the
     * project will be compressed. When isAppendMode is set to false, only images in the specified
     * path will be compressed.
     */
    var resourceDirs: List<String> = listOf(),
)