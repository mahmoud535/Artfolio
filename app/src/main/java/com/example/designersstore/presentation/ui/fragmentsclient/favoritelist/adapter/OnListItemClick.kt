package com.example.designersstore.presentation.ui.fragmentsclient.favoritelist.adapter

import com.example.designersstore.domain.models.CartItem

interface OnListItemClick {
    fun onItemClick(cartItem:CartItem)
  //  fun clickDeleteItem()
}