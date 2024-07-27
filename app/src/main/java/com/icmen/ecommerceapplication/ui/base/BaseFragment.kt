package com.icmen.ecommerceapplication.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private var cachedView: View? = null

    private var viewBinding: VB? = null

    private val mViewBinding: VB?
        get() = viewBinding

    private lateinit var mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cachedView == null) {
            mViewModel = ViewModelProvider(this)[setViewModelClass()]

            viewBinding = setViewBinding()
            cachedView = mViewBinding?.root
            readDataFromArguments()
            initView(savedInstanceState)
            initLogic()
        }
        return cachedView
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun setViewModelClass(): Class<VM>

    abstract fun setViewBinding(): VB

    open fun getViewBinding(): VB? = mViewBinding

    open fun getViewModel(): VM = mViewModel

    open fun readDataFromArguments() {}

    open fun initLogic() {}

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }
}