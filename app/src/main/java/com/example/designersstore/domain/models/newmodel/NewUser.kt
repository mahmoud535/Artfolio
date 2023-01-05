package com.example.designersstore.domain.models.newmodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class NewUser(
        val uid: String,
        val name: String,
        val profileImageUrl: String?
) : Parcelable {
    constructor() : this("", "", "")
}