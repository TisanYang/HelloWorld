package hf.car.wifi.video.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.niklaus.mvvm.base.BaseActivity
import hf.car.wifi.video.utils.HandlerAction

abstract class MBaseActivity<VM : ViewModel, VB : ViewBinding>(val block: (LayoutInflater) -> VB) :
    BaseActivity<VM>(), HandlerAction {

    protected val binding by lazy { block(layoutInflater) }

    protected val mViewModel by lazy { initViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}