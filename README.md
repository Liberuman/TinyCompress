[![image](https://jitpack.io/v/Liberuman/ShadowDrawable.svg)](https://jitpack.io/Liberuman/TinyCompress)
![image](https://img.shields.io/badge/build-passing-brightgreen.svg)
[![image](https://img.shields.io/packagist/l/doctrine/orm.svg)](https://github.com/Liberuman/TinyCompress/blob/master/LICENSE)

[中文](README_CN.md) | [English](README.md)

The Image Optimizer Library is a Gradle plugin developed in Kotlin that automatically scans all image paths in your project and utilizes the TinyPNG API to compress and convert images. It provides various configuration options to customize the optimization process according to your needs.

## Features
- Image compression
- Image format conversion

## Getting Started
To use the Image Optimizer Library in your project, follow these steps:

### Prerequisites
- Obtain a TinyPNG API key. You can sign up for an API key at [TinyPNG API](https://tinypng.com/developers).

### Integration
1. Add the repository URL and plugin dependency to your project's  build.gradle  file:
   groovy
   buildscript {
   repositories {
   // ... other repositories
   maven { url 'https://jitpack.io' }
   }
   dependencies {
   classpath 'com.sxu.plugins.tinycompress:0.1'
   }
   }
2. Apply the plugin in the  build.gradle  file of your app module:
   groovy
   plugins {
   id 'tinycompress'
   }
### Usage
To use this plugin in your project, configure it in the build.gradle file of the module where you applied the plugin:
groovy
pluginConfig {
// Required
apiKey 'xxxx'
}
Refer to the options description below for more details on configuration options.

After configuring, sync your project. You will find two tasks,  compressImage  and  convertImage , in the Gradle panel on the right side of Android Studio. Double-click on them to compress and convert images. You can also execute the tasks directly using Gradle commands:

      // Compress images
      ./gradlew compressImage

      // Convert images
      ./gradlew convertImage

## Configuration Options
The following configuration options are available:

-  apiKey : The TinyPNG API key used for image compression and format conversion. This is a required field.
-  targetFormat : The format to which images will be converted. The default is 'webp'.
-  convertWhiteList : A list of resource file or directory names that should not be converted.
-  skipSize : The size threshold (in bytes) for skipping compression of images. The default is 5k, meaning images smaller than 5k will not be compressed.
-  supportFormat : A list of supported image formats for compression and format conversion. The default supports jpg/jpeg(1), png(2), and webp(4). To support specific formats, add up the values in the parentheses.
-  compressRatioThreshold : The compression ratio threshold. The default is 30, which means the compressed result is used only if the compression ratio exceeds 30%.
-  isAppendMode : Indicates whether to append resourceDirs path to the optimization list. The default is false.
-  whiteList : A list of resource file or directory names that should not be compressed.
-  resourceDirs : A list of paths of resources that need to be compressed. If you want to compress images only in specific paths, you can configure it using this option.

## License
This library is licensed under the [MIT License](LICENSE).

## Acknowledgments
- [TinyPNG](https://tinypng.com) for providing the image compression and format conversion API.