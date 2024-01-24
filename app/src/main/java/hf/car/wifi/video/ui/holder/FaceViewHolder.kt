package hf.car.wifi.video.ui.holder

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hf.car.wifi.video.R
import hf.car.wifi.video.callback.ItemChooseCallback
import hf.car.wifi.video.databinding.ItemFaceBinding
import hf.car.wifi.video.model.PersonModel
import com.niklaus.mvvm.utils.ByteUtil
import hf.car.wifi.video.constant.AppConstant
import hf.car.wifi.video.ui.activity.ModifyFaceActivity
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets

class FaceViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_face, parent, false)
) {

    private val binding = ItemFaceBinding.bind(itemView)

    private val tag: String = javaClass.simpleName

    fun bindTo(model: PersonModel, callback: ItemChooseCallback?) {
        val uId = ByteArray(1)
        val faceIdLenArray = ByteArray(1)
        val uZGZLenArray = ByteArray(1)

        if (model.person.isNotEmpty()) {
            uId[0] = model.person[0]
        }
        if (model.person.size >= 2) {
            faceIdLenArray[0] = model.person[2]
        }

        if (model.person.size >= 3) {
            uZGZLenArray[0] = model.person[3]
        }

        val faceIdLen = ByteUtil.byteArrayToInt(faceIdLenArray)
        val faceId = ByteArray(faceIdLen)
        var faceIdIndex = 0

        val uZGZLen = ByteUtil.byteArrayToInt(uZGZLenArray)
        val uZGZ = ByteArray(uZGZLen)
        var uZGZIndex = 0

        for (i in model.person.indices) {
            if (i in 4 until 4 + faceIdLen) {
                faceId[faceIdIndex] = model.person[i]
                faceIdIndex++
            }
            if (i in 36 until 36 + uZGZLen) {
                uZGZ[uZGZIndex] = model.person[i]
                uZGZIndex++
            }
        }

//        Log.d(tag, "uId:${ByteUtil.byteArrayToInt(uId)}")
//        Log.d(tag, "faceId:${String(faceId, StandardCharsets.UTF_8)}")
//        Log.d(tag, "uZGZ:${String(uZGZ, StandardCharsets.UTF_8)}")

        val tt = StringBuilder()
        tt.append(parent.context.getString(R.string.tv_person_number_s, ByteUtil.byteArrayToInt(uId).toString())).append("\n")
            .append(parent.context.getString(R.string.tv_person_name_s, String(faceId, StandardCharsets.UTF_8)))
//            .append(parent.context.getString(R.string.tv_person_uZGZ_s, String(uZGZ, StandardCharsets.UTF_8)))
        binding.tvId.text = tt.toString()

        binding.btnInfo.setOnClickListener {
            val intent = Intent(parent.context, ModifyFaceActivity::class.java)
            intent.putExtra("uid", ByteUtil.byteArrayToInt(uId))
            intent.putExtra(AppConstant.USER_NAME, String(faceId, StandardCharsets.UTF_8))
            parent.context.startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            callback?.onChoose(ByteUtil.byteArrayToInt(uId), 1)
        }
    }
}