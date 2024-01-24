package hf.car.wifi.video.ui.activity

import android.view.View
import androidx.lifecycle.ViewModelProvider
import hf.car.wifi.video.R
import hf.car.wifi.video.base.EmptyViewModel
import hf.car.wifi.video.base.MBaseActivity
import hf.car.wifi.video.databinding.ActivityBsdMovingBinding
import hf.car.wifi.video.databinding.ActivityGuideBinding

class BsdMovingActivity :
    MBaseActivity<EmptyViewModel, ActivityBsdMovingBinding>(ActivityBsdMovingBinding::inflate) {
    override fun initViewModel(): EmptyViewModel {
        return  ViewModelProvider(this)[EmptyViewModel::class.java]
    }


}
