package com.icmen

import java.util.UUID

class PaymentSDK {

    fun processPayment(amount: Double, cardDetails: CardDetails, callback: PaymentCallback) {
        val isSuccess = simulatePaymentProcessing(amount, cardDetails)

        if (isSuccess) {
            val paymentId = UUID.randomUUID().toString()
            callback.onSuccess("Ödeme başarılı!", paymentId)
        } else {
            callback.onError("Ödeme başarısız!")
        }
    }

    private fun simulatePaymentProcessing(amount: Double, cardDetails: CardDetails): Boolean {
        return amount < 1000
    }

    data class CardDetails(val cardNumber: String, val expiryDate: String, val cvv: String)

    interface PaymentCallback {
        fun onSuccess(message: String, paymentId: String)
        fun onError(errorMessage: String)
    }
}
