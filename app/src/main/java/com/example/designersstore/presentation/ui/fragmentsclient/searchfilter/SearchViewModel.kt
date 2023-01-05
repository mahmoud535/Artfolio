package com.example.designersstore.presentation.ui.fragmentsclient.searchfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designersstore.data.repositoryimp.SearchImp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private var searchImp: SearchImp

    init {
        var firestore = FirebaseFirestore.getInstance()
        searchImp = SearchImp(firestore)
    }

    fun getSearchItemsList(fragmentSearch: SearchFragment) = viewModelScope.launch {
        searchImp.getSearchItemsList(fragmentSearch)
    }

     fun filter(fragmentSearch: SearchFragment)= viewModelScope.launch {
         searchImp.filter(fragmentSearch)
     }
}