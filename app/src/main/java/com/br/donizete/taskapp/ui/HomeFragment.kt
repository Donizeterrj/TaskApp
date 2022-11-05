package com.br.donizete.taskapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.br.donizete.taskapp.R
import com.br.donizete.taskapp.databinding.FragmentHomeBinding
import com.br.donizete.taskapp.databinding.FragmentRegisterBinding
import com.br.donizete.taskapp.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        configTabLayout()

        initClicks()
    }

    private fun initClicks(){


        binding.ibLogout.setOnClickListener {
            logOutUser()
        }
    }

    private fun logOutUser(){
        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_authentication)
    }

    private fun configTabLayout(){
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        adapter.addFragment(TodoFragment(), "A Fazer")
        adapter.addFragment(DoingFragment(), "Fazendo")
        adapter.addFragment(DoneFragment(), "Feitas")

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ){ tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}