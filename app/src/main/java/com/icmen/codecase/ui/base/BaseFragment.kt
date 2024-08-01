package com.icmen.codecase.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.codecase.R
import com.icmen.codecase.ui.activity.MainActivity

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //(activity as? MainActivity)?.setBottomNavigationVisibility(isVisible = showBottomNavigation())
       val bottomNavigation = (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = if (showBottomNavigation()) View.VISIBLE else View.GONE
    }
    abstract fun initView(savedInstanceState: Bundle?)

    open fun showBottomNavigation() = false

    abstract fun setViewBinding(): VB

    open fun getViewBinding(): VB? = mViewBinding

    open fun readDataFromArguments() {}

    open fun initLogic() {}

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }
}