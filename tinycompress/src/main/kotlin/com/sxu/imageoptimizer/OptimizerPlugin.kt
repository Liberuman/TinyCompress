package com.sxu.imageoptimizer

import org.gradle.api.Plugin
import org.gradle.api.Project

/*******************************************************************************
 * The implements of compress images
 *
 * @author: Freeman
 *
 * @date: 2023/4/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
const val taskConfigName = "pluginConfig"

class OptimizerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create(taskConfigName, OptimizerConfig::class.java)
        project.afterEvaluate {
            project.tasks.create("compressImage", CompressTask::class.java)
            project.tasks.create("convertImage", ConvertTask::class.java)
        }
    }
}