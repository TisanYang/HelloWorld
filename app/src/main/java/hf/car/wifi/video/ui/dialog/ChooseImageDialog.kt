package hf.car.wifi.video.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hf.car.wifi.video.callback.DialogClickCallback
import hf.car.wifi.video.databinding.DialogChooseImageBinding

class ChooseImageDialog : DialogFragment() {

    private lateinit var binding: DialogChooseImageBinding

    private var listener: DialogClickCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChooseImageBinding.inflate(LayoutInflater.from(context))

        initView()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val params = dialog?.window?.attributes
        params?.gravity = Gravity.BOTTOM
        dialog?.window?.attributes = params
    }

    private fun initView() {
        binding.btnCamera.setOnClickListener {
            listener?.onClick(1)
            dismiss()
        }

        binding.btnUpdateLocal.setOnClickListener {
            listener?.onClick(2)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setDialogClickCallback(callback: DialogClickCallback) {
        listener = callback
    }
}