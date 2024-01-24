package hf.car.wifi.video.viewmodel

import android.app.Application
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import hf.car.wifi.video.model.PhoneImageModel
import kotlin.concurrent.thread

class LocalImageViewModel(application: Application) : AndroidViewModel(application) {

    val localImageResponse: MutableLiveData<MutableList<PhoneImageModel>> = MutableLiveData()

    fun loadLocalImage() {
        thread {
            val list = mutableListOf<PhoneImageModel>()
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
            )
            val where = MediaStore.Images.Media.MIME_TYPE + "=? or " +
                    MediaStore.Images.Media.MIME_TYPE + "=? or " +
                    MediaStore.Images.Media.MIME_TYPE + "=?"
            val whereArgs = arrayOf("image/jpeg", "image/png", "image/jpg")
            val context: Context = getApplication()
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs,
                MediaStore.Images.Media.DATE_MODIFIED + " desc "
            )
            cursor?.let {
                while (cursor.moveToNext()) {
                    var index = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                    val name = cursor.getString(index)
                    val size =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                    index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val data = cursor.getBlob(index)
                    val location = String(data, 0, data.size - 1)
                    val model = PhoneImageModel(name, size, location)
                    list.add(model)
                }
                cursor.close()
            }

            localImageResponse.postValue(list)
        }
    }
}