package com.niklaus.mvvm.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

abstract class BaseFragment<VM : ViewModel> : Fragment() {

    protected val TAG: String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initParameters()
    }

    protected open fun initParameters() {}

    protected open fun initView() {}

    protected open fun initData() {}

    protected open fun initListener() {}

    protected abstract fun initViewModel(): VM
}