package com.icmen.codecase.ui.fragment.custom

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.icmen.codecase.databinding.FragmentCustomDialogTwoButtonBinding

class CustomDialogWithTwoButtonFragment : DialogFragment() {

    private var _binding: FragmentCustomDialogTwoButtonBinding? = null
    private val binding get() = _binding

    var onYesClicked: (() -> Unit)? = null
    var onNoClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }

        _binding = FragmentCustomDialogTwoButtonBinding.inflate(inflater, container, false)
        return binding?.root ?: View(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setupClickListeners()
    }

    private fun setupDialog() {
        arguments?.let { bundle ->
            binding?.tvTitle?.text = bundle.getString(ARG_TITLE)
            binding?.tvMessage?.text = bundle.getString(ARG_MESSAGE)
        }
    }

    private fun setupClickListeners() {
        binding?.btnYes?.setOnClickListener {
            onYesClicked?.invoke()
            dismiss()
        }
        binding?.btnNo?.setOnClickListener {
            onNoClicked?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun newInstance(title: String, message: String): CustomDialogWithTwoButtonFragment {
            return CustomDialogWithTwoButtonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }
}
