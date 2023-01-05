package com.example.designersstore.presentation.ui.fragmentsclient

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.designersstore.R
import com.example.designersstore.databinding.FragmentLanguageBinding
import java.util.*


class LanguageFragment : BaseFragment() {

    private lateinit var  binding:FragmentLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater)

        setUpView()
        loadLocate()
        return binding.root
    }

    private fun setUpView(){
        binding.ARABIC.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            setLocate("ar")
            recreate()
        }
        binding.ENGLISH.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            setLocate("en")
            recreate()
        }
    }

    private fun recreate(){
        this.getActivity()!!. recreate()
        this.getActivity()!!.finish()
    }

    private fun setLocate(Lang:String){
        val locale= Locale(Lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale
        requireContext().resources.updateConfiguration(config,requireContext().resources.displayMetrics)
        val editor=this.getActivity()!!.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang",Lang)
        editor.apply()
    }
    private fun loadLocate(){
        val sharedPreferences=this.getActivity()!!.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language=sharedPreferences.getString("My_lang", "")
        setLocate(language!!)
    }

}