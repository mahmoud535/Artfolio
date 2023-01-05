package com.example.designersstore.domain.models.repository

import com.example.designersstore.domain.models.AddressClient
import com.example.designersstore.domain.models.CartItem
import com.example.designersstore.domain.models.Order

interface Checkout {
     fun placeAnOrder()
}