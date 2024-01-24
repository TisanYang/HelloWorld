package hf.car.wifi.video.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.niklaus.mvvm.base.BaseFragment

abstract class MBaseFragment<VM : ViewModel, VB : ViewBinding>(val bindingBlack: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    BaseFragment<VM>() {

    private var _binding: VB? = null

    protected val binding get() = _binding!!

    protected val mViewModel by lazy { initViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingBlack(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        Log.d(TAG, "onHiddenChanged hidden:$hidden")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}