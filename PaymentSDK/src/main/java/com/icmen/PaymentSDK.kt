package com.icmen

class PaymentSDK {

    fun processPayment(amount: Double, cardDetails: CardDetails, callback: PaymentCallback) {
        val isSuccess = simulatePaymentProcessing(amount, cardDetails)

        if (isSuccess) {
            callback.onSuccess("Ödeme başarılı!")
        } else {
            callback.onError("Ödeme başarısız!")
        }
    }

    private fun simulatePaymentProcessing(amount: Double, cardDetails: CardDetails): Boolean {
        return amount < 1000
    }

    data class CardDetails(val cardNumber: String, val expiryDate: String, val cvv: String)

    interface PaymentCallback {
        fun onSuccess(message: String)
        fun onError(errorMessage: String)
    }
}
