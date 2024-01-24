package hf.car.wifi.video.ui.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.NumberPicker
import hf.car.wifi.video.R

class NumPickDialog : Dialog {
    private var mContext : Context
    var listener :  TimeChoiceListener ?= null
    constructor(context: Context):super(context){
        mContext = context
        initView()
    }

    private fun initView() {


        val view = LayoutInflater.from(mContext).inflate(R.layout.dia_num_pick, null)
        val picker = view.findViewById<NumberPicker>(R.id.nun_pick)
        val cancelBtn = view.findViewById<Button>(R.id.btn_cancel)
        val sureBtn = view.findViewById<Button>(R.id.btn_confirm)
        picker.minValue = 0
        picker.maxValue = 100

        setContentView(view)

        cancelBtn.setOnClickListener { dismiss() }
        sureBtn.setOnClickListener {
            if (listener != null) {
                listener?.choiceTime(picker.value)
                dismiss()
            }
        }

    }
}

interface TimeChoiceListener{
    fun choiceTime(value: Int)

}