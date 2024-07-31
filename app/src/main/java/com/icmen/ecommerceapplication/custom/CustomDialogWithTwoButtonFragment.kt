package com.icmen.ecommerceapplication.ui.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.icmen.ecommerceapplication.databinding.FragmentCustomDialogTwoButtonBinding

class CustomDialogWithTwoButtonFragment : DialogFragment() {

    private var _binding: FragmentCustomDialogTwoButtonBinding? = null
    private val binding get() = _binding!!

    var onYesClicked: (() -> Unit)? = null
    var onNoClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        _binding = FragmentCustomDialogTwoButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val title = it.getString(ARG_TITLE)
            val message1 = it.getString(ARG_MESSAGE1)

            binding.tvTitle.text = title
            binding.tvMessage.text = message1
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
        private const val ARG_MESSAGE1 = "message1"

        fun newInstance(title: String, message1: String): CustomDialogWithTwoButtonFragment {
            val fragment = CustomDialogWithTwoButtonFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE1, message1)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
