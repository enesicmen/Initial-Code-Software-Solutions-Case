package com.icmen.codecase.ui.fragment.basket

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.icmen.codecase.R
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentBasketBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithTwoButtonFragment
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
                val title = getString(R.string.error)
                setOneButtonDialog(title,it)
            }
        })
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val title = getString(R.string.delete_product)
        val message = getString(R.string.are_you_sure_delete_product)
        val dialog = CustomDialogWithTwoButtonFragment.newInstance(title, message)
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
                val title = getString(R.string.error)
                val message = getString(R.string.basket_is_empty)
                setOneButtonDialog(title,message)
            }
        }
    }

    private fun setOneButtonDialog(title: String, message: String){
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
}
