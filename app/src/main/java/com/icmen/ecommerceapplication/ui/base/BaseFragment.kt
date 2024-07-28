package com.icmen.ecommerceapplication.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var cachedView: View? = null

    private var viewBinding: VB? = null

    private val mViewBinding: VB?
        get() = viewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cachedView == null) {
            viewBinding = setViewBinding()
            cachedView = mViewBinding?.root
            readDataFromArguments()
            initView(savedInstanceState)
            initLogic()
        }
        return cachedView
    }

    abstract fun initView(savedInstanceState: Bundle?)


    abstract fun setViewBinding(): VB

    open fun getViewBinding(): VB? = mViewBinding

    open fun readDataFromArguments() {}

    open fun initLogic() {}

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }
}