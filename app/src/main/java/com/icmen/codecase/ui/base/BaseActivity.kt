package com.icmen.codecase.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var viewBinding: VB? = null

    private val mViewBinding: VB?
        get() = viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = setViewBinding()
        setContentView(mViewBinding?.root)
        initView(savedInstanceState)
        initObservers()
        initLogic()
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun setViewBinding(): VB

    open fun getViewBinding(): VB? = mViewBinding

    open fun initObservers() {}

    open fun initLogic() {}

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }
}
