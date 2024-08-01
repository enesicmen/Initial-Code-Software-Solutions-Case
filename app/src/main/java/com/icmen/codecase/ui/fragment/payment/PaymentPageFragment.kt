package com.icmen.codecase.ui.fragment.payment

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.icmen.PaymentSDK
import com.icmen.codecase.R
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentPaymentBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        val payButton: Button? = getViewBinding()?.payButton

        setTotalAmount(mTotalAmount)
        payButton?.setOnClickListener {
            val amount = mTotalAmount.toDouble()
            val cardNumber = getViewBinding()?.cardNumber?.text.toString()
            val expiryDate = getViewBinding()?.expiryDate?.text.toString()
            val cvv = getViewBinding()?.cvv?.text.toString()

            if (amount > 0 && cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
                val cardDetails = PaymentSDK.CardDetails(cardNumber, expiryDate, cvv)
                paymentSDK.processPayment(amount, cardDetails, object : PaymentSDK.PaymentCallback {
                    override fun onSuccess(message: String, paymentId: String) {
                        showToast(message)
                        getViewModel()?.saveOrderToFirebase(paymentId, mProducts, mTotalAmount, mUserAddress)
                        getViewModel()?.orderSaveStatus?.observe(viewLifecycleOwner) { isSuccess ->
                            if (isSuccess) {
                                showToast("Siparişiniz başarıyla kaydedildi.")
                                clearBasket()
                                navigateToSuccessPage()
                            } else {
                                showToast("Sipariş kaydedilirken hata oluştu.")
                            }
                        }
                    }

                    override fun onError(errorMessage: String) {
                        showToast(errorMessage)
                    }
                })
            } else {
                showToast("Lütfen tüm alanları doğru bir şekilde doldurun.")
            }
        }

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

    private fun setTotalAmount(totalAmount: String) {
        getViewBinding()?.tvTotalAmount?.text = totalAmount
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun clearBasket() {
        getViewModel()?.clearBasket()
    }

    private fun navigateToSuccessPage() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.paymentPageFragment, true)
            .build()
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
}
