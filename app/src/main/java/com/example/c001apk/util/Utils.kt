package com.example.c001apk.util

import android.app.DownloadManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Environment
import com.example.c001apk.MyApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest
import kotlin.random.Random


object Utils {

    fun downloadApk(context: Context, url: String, name: String) {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager
            .Request(Uri.parse(url))
            .setMimeType("application/vnd.android.package-archive")
            .setTitle(name)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        downloadManager.enqueue(request)
        ClipboardUtil.copyText(context, url, false)
    }

    /**
     * 根据字符串生成Base64码
     *
     * @receiver 原生字符串
     * @param isRaw 是否省略“=”符号
     * @return Base64码
     */
    fun String.getBase64(isRaw: Boolean = true): String {
        var result =
            Base64Utils.encode(this.toByteArray())//Base64.getEncoder().encodeToString(this.toByteArray())
        if (isRaw) {
            result = result.replace("=", "")
        }
        return result
    }

    /**
     * 根据字符串生成MD5值
     *
     * @receiver 原生字符串
     * @return MD5值
     */
    fun String.getMD5(): String {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")

        val digest = instance.digest(this.toByteArray())

        val sb = StringBuffer()

        for (b in digest) {
            val i = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }

        return sb.toString().replace("-", "")
    }


    fun getInstalledAppMd5(appInfo: ApplicationInfo): String {

        return generateRandomMD5()
        // 猜测MD5与增量更新有关，因不计划支持，且获取MD5耗时较长，现随机生成

//            try {
//                // 获取应用程序的APK文件路径
//                val apkPath = appInfo.sourceDir
//
//                // 计算MD5值
//                return FileInputStream(File(apkPath)).calculateMd5()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return ""
    }

    fun generateRandomMD5(): String {
        val hexChars = "0123456789abcdef"
        val stringBuilder = StringBuilder(32)

        repeat(32) {
            val randomIndex = Random.nextInt(hexChars.length)
            stringBuilder.append(hexChars[randomIndex])
        }

        return stringBuilder.toString()
    }

    /**
     * 随机生成Mac地址
     *
     * @return Mac地址
     */
    fun randomMacAddress(): String {
        val sb = StringBuilder()

        for (i in 0..5) {
            if (sb.isNotEmpty()) {
                sb.append(":")
            }
            val value = Random.nextInt(256)
            val element = Integer.toHexString(value)
            if (element.length < 2) {
                sb.append(0)
            }
            sb.append(element)
        }

        return sb.toString().uppercase()
    }

    fun randomManufacturer(): String {
        val manufacturers = listOf(
            "Samsung",
            "Google",
            "Huawei",
            "Xiaomi",
            "OnePlus",
            "Sony",
            "LG",
            "Motorola",
            "HTC",
            "Nokia",
            "Lenovo",
            "Asus",
            "ZTE",
            "Alcatel",
            "OPPO",
            "Vivo",
            "Realme"
            // 添加更多制造商名称
        )

        return manufacturers[Random.nextInt(manufacturers.size)]
    }

    fun randomBrand(): String {
        val brands = listOf(
            "Samsung",
            "Google",
            "Huawei",
            "Xiaomi",
            "Redmi",
            "OnePlus",
            "Sony",
            "LG",
            "Motorola",
            "HTC",
            "Nokia",
            "Lenovo",
            "Asus",
            "ZTE",
            "Alcatel",
            "OPPO",
            "Vivo",
            "Realme"
            // 添加更多品牌名称
        )

        return brands[Random.nextInt(brands.size)]
    }


    fun randomDeviceModel(): String {
        val assetManager = MyApplication.context.assets

        try {
            val inputStream = assetManager.open("devicemodel.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = ArrayList<String>()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                lines.add(line ?: "")
            }

            val randomIndex = Random.nextInt(lines.size)
            return lines[randomIndex]
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "null"
    }

    fun randomSdkInt(): String {
        return Random.nextInt(21, 34).toString()
    }

    fun randomAndroidVersionRelease(): String {
        val androidVersionRelease = listOf(
            "5.0.1", // Lollipop
            "6.0",   // Marshmallow
            "7.0",   // Nougat
            "7.1.1", // Nougat
            "8.0.0", // Oreo
            "8.1.0", // Oreo
            "9",     // Pie
            "10",    // Android 10
            "11",    // Android 11
            "12",     // Android 12
            "13",
            "14",
        )

        return androidVersionRelease[Random.nextInt(androidVersionRelease.size)]
    }

}