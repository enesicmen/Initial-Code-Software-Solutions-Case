package com.icmen.ecommerceapplication.ui.fragment.payment

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.icmen.PaymentSDK
import com.icmen.ecommerceapplication.databinding.FragmentPaymentBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment

class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {


    private var mTotalAmount : String = ""
    private val paymentSDK = PaymentSDK()

    override fun initView(savedInstanceState: Bundle?) {
        val payButton: Button? = getViewBinding()?.payButton

        setTotalAmount(mTotalAmount)
        payButton?.setOnClickListener {
            val amount = mTotalAmount.toDouble()
            val cardNumber = getViewBinding()?.cardNumber?.text.toString()
            val expiryDate = getViewBinding()?.expiryDate?.text.toString()
            val cvv = getViewBinding()?.cvv?.text.toString()

            if (amount != null) {
                val cardDetails = PaymentSDK.CardDetails(cardNumber, expiryDate, cvv)
                paymentSDK.processPayment(amount, cardDetails, object : PaymentSDK.PaymentCallback {
                    override fun onSuccess(message: String) {
                        showToast(message)
                    }

                    override fun onError(errorMessage: String) {
                        showToast(errorMessage)
                    }
                })
            } else {
                showToast("Geçerli bir tutar girin.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun setViewBinding(): FragmentPaymentBinding =
        FragmentPaymentBinding.inflate(layoutInflater)


    override fun readDataFromArguments() {
        super.readDataFromArguments()
        arguments?.let {
            val safeArgs = PaymentFragmentArgs.fromBundle(it)
            mTotalAmount = safeArgs.amount
        }
    }

    private fun setTotalAmount(totalAmount:String){
        getViewBinding()?.tvTotalAmount?.text = totalAmount
    }

}
