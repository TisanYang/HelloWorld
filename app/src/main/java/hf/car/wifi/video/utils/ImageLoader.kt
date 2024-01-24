package hf.car.wifi.video.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import hf.car.wifi.video.R
import hf.car.wifi.video.base.GlideApp
import hf.car.wifi.video.callback.ImageLoadResponse
import java.io.File

object ImageLoader {

    fun downloadFile(any: Any, url: String, listener: RequestListener<File>) {
        when (any) {
            is Context -> {
                GlideApp.with(any).downloadOnly().load(url).addListener(listener).preload()
            }
            is Activity -> {
                GlideApp.with(any).downloadOnly().load(url).addListener(listener).preload()
            }
            is Fragment -> {
                GlideApp.with(any).downloadOnly().load(url).addListener(listener).preload()
            }
        }
    }

    fun downloadBitmap(any: Any, url: String, listener: CustomTarget<Bitmap>) {
        when (any) {
            is Context -> {
                GlideApp.with(any).asBitmap().load(url).into(listener)
            }
            is Activity -> {
                GlideApp.with(any).asBitmap().load(url).into(listener)
            }
            is Fragment -> {
                GlideApp.with(any).asBitmap().load(url).into(listener)
            }
        }
    }

    fun loadImage(any: Any, url: String, imageView: ImageView) {
        when (any) {
            is Context -> {
                GlideApp.with(any).load(url).placeholder(R.drawable.default_image)
                    .error(R.drawable.load_error).into(imageView)
            }
            is Activity -> {
                GlideApp.with(any).load(url).placeholder(R.drawable.default_image)
                    .error(R.drawable.load_error).into(imageView)
            }
            is Fragment -> {
                GlideApp.with(any).load(url).placeholder(R.drawable.default_image)
                    .error(R.drawable.load_error).into(imageView)
            }
        }
    }

    fun loadImage(
        any: Any,
        url: String,
        imageView: ImageView,
        defaultRes: Int,
        errorRes: Int
    ) {
        when (any) {
            is Context -> {
                GlideApp.with(any).load(url).placeholder(defaultRes).error(errorRes).into(imageView)
            }
            is Activity -> {
                GlideApp.with(any).load(url).placeholder(defaultRes).error(errorRes).into(imageView)
            }
            is Fragment -> {
                GlideApp.with(any).load(url).placeholder(defaultRes).error(errorRes).into(imageView)
            }
        }
    }

    fun loadImage(
        any: Any, url: String,
        imageView: ImageView, response: ImageLoadResponse
    ) {
        when (any) {
            is Context -> {
                GlideApp.with(any).asBitmap().load(url)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadSuccess(resource)
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadFailed()
                            return false
                        }
                    }).into(imageView)
            }
            is Activity -> {
                GlideApp.with(any).asBitmap().load(url)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadSuccess(resource)
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadFailed()
                            return false
                        }
                    }).into(imageView)
            }
            is Fragment -> {
                GlideApp.with(any).asBitmap().load(url)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadSuccess(resource)
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadFailed()
                            return false
                        }
                    }).into(imageView)
            }
        }
    }

    fun loadImage(
        any: Any,
        url: String,
        imageView: ImageView,
        defaultRes: Int,
        errorRes: Int, response: ImageLoadResponse
    ) {
        when (any) {
            is Context -> {
                GlideApp.with(any).asBitmap().load(url).placeholder(defaultRes).error(errorRes)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadSuccess(resource)
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadFailed()
                            return false
                        }
                    }).into(imageView)
            }
            is Activity -> {
                GlideApp.with(any).asBitmap().load(url).placeholder(defaultRes).error(errorRes)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadSuccess(resource)
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadFailed()
                            return false
                        }
                    }).into(imageView)
            }
            is Fragment -> {
                GlideApp.with(any).asBitmap().load(url).placeholder(defaultRes).error(errorRes)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadSuccess(resource)
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            response.loadFailed()
                            return false
                        }
                    }).into(imageView)
            }
        }
    }
}