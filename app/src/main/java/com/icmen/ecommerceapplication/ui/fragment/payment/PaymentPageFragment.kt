package com.icmen.ecommerceapplication.ui.fragment.payment

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.icmen.PaymentSDK
import com.icmen.ecommerceapplication.data.model.Order
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentPaymentBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PaymentPageFragment : BaseFragment<FragmentPaymentBinding>() {

    private var mTotalAmount: String = ""
    private lateinit var mProducts: Array<Product>
    private val paymentSDK = PaymentSDK()

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
                        saveOrderToFirebase(paymentId)
                        clearBasket()
                        navigateToSuccessPage()
                    }

                    override fun onError(errorMessage: String) {
                        showToast(errorMessage)
                    }
                })
            } else {
                showToast("Lütfen tüm alanları doğru bir şekilde doldurun.")
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
            val safeArgs = PaymentPageFragmentArgs.fromBundle(it)
            mTotalAmount = safeArgs.amount
            mProducts = safeArgs.products
        }
    }

    private fun setTotalAmount(totalAmount: String) {
        getViewBinding()?.tvTotalAmount?.text = totalAmount
    }

    private fun saveOrderToFirebase(paymentId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ordersRef = FirebaseFirestore.getInstance().collection("orders").document(userId).collection("userOrders")

            val newOrderRef = ordersRef.document()

            val orderData = hashMapOf(
                "products" to mProducts.toList(),
                "totalAmount" to mTotalAmount,
                "paymentId" to paymentId,
                "orderDate" to System.currentTimeMillis()
            )

            newOrderRef.set(orderData)
                .addOnSuccessListener {
                    showToast("Siparişiniz başarıyla kaydedildi.")
                }
                .addOnFailureListener { e ->
                    showToast("Sipariş kaydedilirken hata oluştu: ${e.message}")
                }
        } else {
            showToast("Önce giriş yapmalısınız.")
        }
    }

    private fun clearBasket() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val basketRef = FirebaseFirestore.getInstance().collection("basket").document(userId).collection("products")

            basketRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        document.reference.delete()
                    }
                    showToast("Sepetiniz başarıyla silindi.")
                } else {
                    showToast("Sepet silinirken hata oluştu: ${task.exception?.message}")
                }
            }
        }
    }

    private fun navigateToSuccessPage() {
        // Başarılı ödeme sonrası yönlendirme işlemini gerçekleştirin

    }
}
