[![image](https://jitpack.io/v/Liberuman/ShadowDrawable.svg)](https://jitpack.io/Liberuman/TinyCompress)
![image](https://img.shields.io/badge/build-passing-brightgreen.svg)
[![image](https://img.shields.io/packagist/l/doctrine/orm.svg)](https://github.com/Liberuman/TinyCompress/blob/master/LICENSE)

[中文](README_CN.md) | [English](README.md)

图片优化库是一个使用Kotlin语言开发的gradle插件，会自动扫描项目中的所有图片路径, 利用 TinyPNG API 对图片进行压缩和格式转换。它提供了各种配置选项，可以根据您的需求定制优化过程。

## 功能

- 图片压缩
- 图片格式转换

## 入门指南

要在项目中使用图片优化库，请按照以下步骤进行操作：

### 先决条件

- 获取 TinyPNG API 密钥。您可以在 [TinyPNG API](https://tinypng.com/developers) 上注册并获取 API 密钥。

### 引入

1. 在项目的  build.gradle  文件中添加仓库地址, 并引入插件:


    buildscript {

        ...其他仓库地址

        maven { url 'https://jitpack.io' }
   
        dependencies {
            classpath 'com.github.Liberuman:TinyCompress:0.2'
        }
    }


2. 在app module模块的build.gradle的头部引用插件:
   

    plugins {
       id 'tinycompress'
    }

### 使用方法

要在项目中使用此插件，请先在引入此插件的 module 的 build.gradle 中进行配置：

    pluginConfig {
        // 必需
        apiKey 'xxxx'
    }

具体配置选项的详细信息，请参阅后面的选项说明。

配置完成后同步项目, 然后右侧的gradle面板中的 项目名-app-Tasks-plugin 中就会出现 compressImage 和 covertImage 两个任务了, 
双击即可实现图片的压缩与转换. 当然也可通过gradle命令直接执行任务:

    // 压缩图片
    ./gradlew compressImage
    
    // 转换图片
    ./gradlew convertImage

## 配置选项

提供以下配置选项：
- apiKey ：用于图片压缩和格式转换的 TinyPNG API 密钥, 必填项;
- targetFormat ：将图片转换为指定的格式, 默认为webp;
- convertWhiteList ：不参与转换的资源文件或目录列表;
- skipSize ：忽略压缩的图片大小（以字节为单位）, 默认为5k, 即小于5k的图片不参与压缩;
- supportFormat ：支持的图片格式列表，用于压缩和格式转换, 默认同时支持jpg/jpeg(1), png(2), webp(4), 如果只支持某一个或几个格式, 取前面格式括号中的值进行相加即可;

- compressRatioThreshold ：图片压缩阈值, 默认为30, 表示压缩比超过30%才采用压缩后的结果;
- isAppendMode ：指示是否将 resourceDirs 路径追加到优化列表中, 默认为false;
- whiteList ：不应压缩的资源文件或目录名称列表;
- resourceDirs ：需要压缩的资源路径列表, 如果只想压缩指定路径的图片, 可通过此选项配置;

## 许可证

该库在 [MIT 许可证](LICENSE) 下发布。

## 致谢

- [TinyPNG](https://tinypng.com) 提供图片压缩&格式转化 API。