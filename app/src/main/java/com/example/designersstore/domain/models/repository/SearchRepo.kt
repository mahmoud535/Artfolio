package com.example.designersstore.domain.models.repository

import com.example.designersstore.presentation.ui.fragmentsclient.searchfilter.SearchFragment

interface SearchRepo {

    suspend fun getSearchItemsList(fragmentSearch: SearchFragment)

    suspend fun filter(fragmentSearch: SearchFragment)
}