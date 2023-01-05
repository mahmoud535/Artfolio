package com.example.designersstore.domain.models.repository

import android.app.Activity

interface AddEditAddressRepository {

    suspend fun parcelableAddress(activity: Activity)
}