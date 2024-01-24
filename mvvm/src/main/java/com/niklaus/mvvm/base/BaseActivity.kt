package com.niklaus.mvvm.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    protected val TAG: String = javaClass.simpleName

    protected open fun initView() {}

    protected open fun initData() {}

    protected open fun initListener() {}

    protected open fun loadData() {}

    protected abstract fun initViewModel(): VM
}