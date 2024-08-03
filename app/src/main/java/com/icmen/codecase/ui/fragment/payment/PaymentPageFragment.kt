package com.icmen.codecase.ui.fragment.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.icmen.PaymentSDK
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentPaymentBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentPageFragment : BaseFragment<FragmentPaymentBinding, PaymentPageViewModel>() {

    private var mTotalAmount: String = ""
    private var mUserAddress: String = ""
    private lateinit var mProducts: Array<Product>
    private val paymentSDK = PaymentSDK()

    override fun setViewModelClass() = PaymentPageViewModel::class.java

    override fun setViewBinding(): FragmentPaymentBinding =
        FragmentPaymentBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        setTotalAmount(mTotalAmount)
        pay()

        observeOrderSaveStatus()
        setCardNumber()
        setExpiryDate()
        onBackPressedDispatcher()
    }

    private fun pay(){
        getViewBinding()?.btnPay?.setOnClickListener {
            val amount = mTotalAmount.toDouble()
            val cardNumber = getViewBinding()?.etCardNumber?.text.toString()
            val expiryDate = getViewBinding()?.etExpiryDate?.text.toString()
            val cvv = getViewBinding()?.etCvv?.text.toString()

            if (amount > 0 && cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
                val cardDetails = PaymentSDK.CardDetails(cardNumber, expiryDate, cvv)
                paymentSDK.processPayment(amount, cardDetails, object : PaymentSDK.PaymentCallback {
                    override fun onSuccess(message: String, paymentId: String) {
                        getViewModel()?.saveOrderToFirebase(paymentId, mProducts, mTotalAmount, mUserAddress)
                    }

                    override fun onError(errorMessage: String) {
                        val title = getString(R.string.error)
                        setOneButtonDialog(title,errorMessage)
                    }
                })
            } else {
                val title = getString(R.string.error)
                val message = getString(R.string.fill_in_all_fields)
                setOneButtonDialog(title,message)
            }
        }
    }
    private fun onBackPressedDispatcher(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToProductsPageAndClearBackStack()
                }
            }
        )
    }
    override fun readDataFromArguments() {
        super.readDataFromArguments()
        arguments?.let {
            val safeArgs = PaymentPageFragmentArgs.fromBundle(it)
            mTotalAmount = safeArgs.amount
            mProducts = safeArgs.products
            mUserAddress = safeArgs.userAddress
        }
    }

    private fun observeOrderSaveStatus() {
        getViewModel()?.orderSaveStatus?.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    clearBasket()
                    navigateToSuccessPage()
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private fun setTotalAmount(totalAmount: String) {
        getViewBinding()?.tvTotalAmount?.text = getString(R.string.total_amount_payment,totalAmount + "TL")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearBasket() {
        getViewModel()?.clearBasket()
    }

    private fun navigateToSuccessPage() {
        val title = getString(R.string.success)
        val message = getString(R.string.order_success)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.paymentPageFragment, true)
            .build()
        setOneButtonDialog(title, message)
        findNavController().navigate(R.id.action_paymentPageFragment_to_productsPageFragment, null, navOptions)
        updateBottomNavigationView()
    }

    private fun navigateToProductsPageAndClearBackStack() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.paymentPageFragment, true)
            .build()
        findNavController().navigate(R.id.productsPageFragment, null, navOptions)
        updateBottomNavigationView()
    }

    private fun updateBottomNavigationView() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.selectedItemId = R.id.menu_item_home
    }

    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }

    private fun setCardNumber(){
        getViewBinding()?.etCardNumber?.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                val cleanString = s.toString().replace(" ", "")
                val formattedString = StringBuilder()

                for (i in cleanString.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formattedString.append(" ")
                    }
                    formattedString.append(cleanString[i])
                }

                getViewBinding()?.etCardNumber?.setText(formattedString.toString())
                getViewBinding()?.etCardNumber?.setSelection(formattedString.length)
                isFormatting = false
            }
        })
    }

    private fun setExpiryDate(){
        getViewBinding()?.etExpiryDate?.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                val cleanString = s.toString().replace("/", "")
                val formattedString = StringBuilder()

                for (i in cleanString.indices) {
                    if (i == 2 && cleanString.length >= 2) {
                        formattedString.append("/")
                    }
                    formattedString.append(cleanString[i])
                }

                getViewBinding()?.etExpiryDate?.setText(formattedString.toString())
                getViewBinding()?.etExpiryDate?.setSelection(formattedString.length)
                isFormatting = false
            }
        })
    }
}
