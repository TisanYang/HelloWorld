package com.niklaus.mvvm.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.ExifInterface
import android.media.Image
import android.util.Log
import java.io.ByteArrayOutputStream

/**
 * 图像相关处理工具
 */
object BitmapUtil {

    private val TAG: String = javaClass.simpleName

    /**
     * 图片缩放
     * @param imageByte 原图
     * @param size 缩放比例（值1为原图。值越大，压缩图片越小1-20）
     */
    fun scaleBitmap(imageByte: ByteArray?, size: Int): Bitmap? {
        if (null == imageByte) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inSampleSize = size
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size, options)
    }

    /**
     * 图片旋转
     * @param bitmap 原图
     * @param rotate 旋转角度
     * (source-产生子位图的源位图、x-子位图第一个像素在源位图的X坐标、y-子位图第一个像素在源位图的y坐标、
     * width-子位图每一行的像素个数、height-子位图的行数、matrix-对像素值进行变换的可选矩阵、
     * filter-如果为true，源图要被过滤，该参数仅在matrix包含了超过一个翻转才有效)
     */
    fun rotateBitmap(bitmap: Bitmap?, rotate: Float): Bitmap? {
        if (null == bitmap) {
            return null
        }
        Log.i(TAG, "bitmap rotate:$rotate")
        val matrix = Matrix()
        matrix.postRotate(rotate)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 旋转图片
     */
    fun rotateBitmap(bitmap: Bitmap, rotate: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val mtx = Matrix()
        mtx.postRotate(rotate.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    /**
     * 获取照片旋转角度
     */
    fun getPictureDegree(imagePath: String): Int {
        var rotate = 0
        try {
            val exif = ExifInterface(imagePath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            Log.d(TAG, "getPictureDegree orientation:$orientation")
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270,
                ExifInterface.ORIENTATION_TRANSVERSE -> rotate = 270

                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    /**
     * 图片转化(Image转化成Bitmap)
     * @param image 原图
     */
    fun imageToBitmap(image: Image?): Bitmap? {
        if (null == image) {
            return null
        }
        val imageFormat = image.format
        Log.i(TAG, "image format:$imageFormat")
        if (ImageFormat.JPEG == imageFormat) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        } else if (ImageFormat.YUV_420_888 == imageFormat) {
            val yPlane = image.planes[0]
            val uPlane = image.planes[1]
            val vPlane = image.planes[2]

            val yBuffer = yPlane.buffer
            val uBuffer = uPlane.buffer
            val vBuffer = vPlane.buffer

            val numPixels: Int = (image.width * image.height * 1.5f).toInt()
            val nv21 = ByteArray(numPixels)
            var index = 0

            val yRowStride = yPlane.rowStride
            val yPixelStride = yPlane.pixelStride
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    nv21[index++] = yBuffer[y * yRowStride + x * yPixelStride]
                }
            }

            val uvRowStride = uPlane.rowStride
            val uvPixelStride = uPlane.pixelStride
            val uvWidth = image.width / 2
            val uvHeight = image.height / 2
            for (y in 0 until uvHeight) {
                for (x in 0 until uvWidth) {
                    val bufferIndex = y * uvRowStride + x * uvPixelStride
                    // V channel.
                    nv21[index++] = vBuffer[bufferIndex]
                    // U channel.
                    nv21[index++] = uBuffer[bufferIndex]
                }
            }
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val outputSteam = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 85, outputSteam)
            val imageByte = outputSteam.toByteArray()
            return scaleBitmap(imageByte, 1)
        } else {
            return null
        }
    }

    /**
     * 获取Bitmap大小
     * @param bitmap 原图
     */
    fun bitmapSize(bitmap: Bitmap?): Double {
        var size = 0.00
        if (null != bitmap) {
            size = (bitmap.allocationByteCount / 1024).toDouble()
        }
        Log.d(TAG, "bitmap size:" + size + "KB")
        return size
    }

    /**
     * bitmap转byte[]
     */
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * byte[]转bitmap
     */
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        Log.d(TAG, "byteArrayToBitmap byteArray size:${byteArray.size}")
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    /**
     * 图片压缩
     * @param image 原图
     * @param maxSize 压缩到大小
     */
    fun compressImage(image: Bitmap, maxSize: Long): Bitmap {
        val byteCount = image.byteCount
        Log.i("yc压缩图片", "压缩前大小$byteCount")
        val baos = ByteArrayOutputStream()
        // 把ByteArrayInputStream数据生成图片
        // 质量压缩方法，options的值是0-100，这里100表示原来图片的质量，不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var options = 90
        // 循环判断如果压缩后图片是否大于maxSize,大于继续压缩
        while (baos.toByteArray().size > maxSize) {
            // 重置baos即清空baos
            baos.reset()
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)
            // 每次都减少10，当为1的时候停止，options<10的时候，递减1
            options -= if (options == 1) {
                break
            } else if (options <= 10) {
                1
            } else {
                10
            }
        }
        val bytes = baos.toByteArray()
        if (bytes.isNotEmpty()) {
            // 把压缩后的数据baos存放到bytes中
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val byteCount1 = bitmap.byteCount
            Log.i("yc压缩图片", "压缩后大小$byteCount1")
            return bitmap
        }
        return image
    }

    /**
     * 第一种：质量压缩法
     * @param src 源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle 是否回收
     */
    fun compressByQuality(src: Bitmap, maxByteSize: Long, recycle: Boolean): Bitmap {
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        Log.i("yc压缩图片", "压缩前大小" + baos.toByteArray().size)
        val bytes: ByteArray
        if (baos.size() <= maxByteSize) { // 最好质量的不大于最大字节，则返回最佳质量
            bytes = baos.toByteArray()
        } else {
            baos.reset()
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos)
            if (baos.size() >= maxByteSize) { // 最差质量不小于最大字节，则返回最差质量
                bytes = baos.toByteArray()
            } else {
                // 二分法寻找最佳质量
                var st = 0
                var end = 100
                var mid = 0
                while (st < end) {
                    mid = (st + end) / 2
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos)
                    val len = baos.size()
                    if (len.toLong() == maxByteSize) {
                        break
                    } else if (len > maxByteSize) {
                        end = mid - 1
                    } else {
                        st = mid + 1
                    }
                }
                if (end == mid - 1) {
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos)
                }
                bytes = baos.toByteArray()
            }
        }
        if (recycle && !src.isRecycled) {
            src.recycle()
        }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Log.i("yc压缩图片", "压缩后大小" + bytes.size)
        return bitmap
    }

    /**
     * 第一种：质量压缩法
     * @param src 源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle 是否回收
     */
    fun compressByQualityByteArray(src: Bitmap, maxByteSize: Long, recycle: Boolean): ByteArray {
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        Log.i("yc压缩图片", "待压缩大小" + baos.toByteArray().size)
        val bytes: ByteArray
        if (baos.size() <= maxByteSize) { // 最好质量的不大于最大字节，则返回最佳质量
            bytes = baos.toByteArray()
        } else {
            baos.reset()
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos)
            if (baos.size() >= maxByteSize) { // 最差质量不小于最大字节，则返回最差质量
                bytes = baos.toByteArray()
            } else {
                // 二分法寻找最佳质量
                var st = 0
                var end = 100
                var mid = 0
                while (st < end) {
                    mid = (st + end) / 2
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos)
                    val len = baos.size()
                    if (len.toLong() == maxByteSize) {
                        break
                    } else if (len > maxByteSize) {
                        end = mid - 1
                    } else {
                        st = mid + 1
                    }
                }
                if (end == mid - 1) {
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos)
                }
                bytes = baos.toByteArray()
            }
        }
        if (recycle && !src.isRecycled) {
            src.recycle()
        }
        Log.i("yc压缩图片", "压缩完成大小" + bytes.size)
        return bytes
    }
}