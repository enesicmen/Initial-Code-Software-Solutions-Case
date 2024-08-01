package com.icmen.codecase.ui.fragment.basket

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentBasketBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.dialog.CustomDialogWithOneButtonFragment
import com.icmen.codecase.ui.dialog.CustomDialogWithTwoButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketPageFragment : BaseFragment<FragmentBasketBinding, BasketPageViewModel>() {

    private lateinit var basketAdapter: BasketPageAdapter
    private val basketItems: MutableList<Product> = mutableListOf()

    override fun setViewBinding(): FragmentBasketBinding =
        FragmentBasketBinding.inflate(layoutInflater)

    override fun setViewModelClass() = BasketPageViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        getViewBinding()?.rvBasket?.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketPageAdapter(basketItems, { }) { position, newQuantity ->
            if (newQuantity == 0) {
                showDeleteConfirmationDialog(position)
            } else {
                getViewModel()?.updateBasketItemQuantity(position, newQuantity)
            }
        }
        getViewBinding()?.rvBasket?.adapter = basketAdapter

        observeViewModel()
        openPaymentPage()
    }

    private fun observeViewModel() {
        getViewModel()?.basketItemsLiveData?.observe(viewLifecycleOwner, Observer { items ->
            basketItems.clear()
            basketItems.addAll(items)
            basketAdapter.notifyDataSetChanged()
        })

        getViewModel()?.totalAmountLiveData?.observe(viewLifecycleOwner, Observer { totalAmount ->
            getViewBinding()?.tvTotalAmount?.text = String.format("%.2f TL", totalAmount)
        })

        getViewModel()?.userAddressLiveData?.observe(viewLifecycleOwner, Observer { address ->

        })

        getViewModel()?.progressVisibility?.observe(viewLifecycleOwner, Observer { isVisible ->
            getViewBinding()?.progressBar?.visibility = if (isVisible) View.VISIBLE else View.GONE
        })

        getViewModel()?.errorLiveData?.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                val dialog = CustomDialogWithOneButtonFragment.newInstance("HATA", it)
                dialog.onOkClicked = {}
                dialog.show(requireActivity().supportFragmentManager, "customDialog")
            }
        })
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val dialog = CustomDialogWithTwoButtonFragment.newInstance("Ürünü Sil", "Bu ürünü sepetten silmek ister misiniz ? ")
        dialog.onYesClicked = {
            getViewModel()?.deleteBasketItem(position)
        }
        dialog.onNoClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }

    private fun openPaymentPage() {
        getViewBinding()?.btnBuy?.setOnClickListener {
            if (basketItems.isNotEmpty()) {
                getViewModel()?.userAddressLiveData?.observe(viewLifecycleOwner) { userAddress ->
                    val actionDetail = BasketPageFragmentDirections.actionBasketPageFragmentToPaymentPageFragment(
                        products = basketItems.toTypedArray(),
                        amount = getViewModel()?.totalAmountLiveData?.value.toString(),
                        userAddress = userAddress
                    )
                    findNavController().navigate(actionDetail)
                }
            } else {
                Toast.makeText(context, "Sepetiniz boş", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
