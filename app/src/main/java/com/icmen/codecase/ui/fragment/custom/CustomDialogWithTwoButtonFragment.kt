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
    private val binding get() = _binding!!
    var onYesClicked: (() -> Unit)? = null
    var onNoClicked: (() -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }
        _binding = FragmentCustomDialogTwoButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            val title = bundle.getString(ARG_TITLE)
            val message = bundle.getString(ARG_MESSAGE)

            binding.let {
                it.tvTitle.text = title
                it.tvMessage.text = message
            }
        }
        binding.btnYes.setOnClickListener {
            onYesClicked?.invoke()
            dismiss()
        }
        binding.btnNo.setOnClickListener {
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
            val fragment = CustomDialogWithTwoButtonFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
