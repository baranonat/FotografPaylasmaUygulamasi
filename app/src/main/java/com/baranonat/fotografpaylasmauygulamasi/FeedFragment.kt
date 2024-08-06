package com.baranonat.fotografpaylasmauygulamasi

import android.os.Bundle
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation
import com.baranonat.fotografpaylasmauygulamasi.databinding.FragmentFeedBinding
import com.baranonat.fotografpaylasmauygulamasi.databinding.FragmentKullaniciBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FeedFragment : Fragment(),PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var popup:PopupMenu
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener { floatingButtonTiklandi(it) }
        popup=PopupMenu(requireContext(),binding.floatingActionButton)
        val inflate=popup.menuInflater
        inflate.inflate(R.menu.pop_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)

    }

    fun floatingButtonTiklandi(view:View){
        popup.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(menu: MenuItem?): Boolean {
        if(menu?.itemId==R.id.yuklemeItem){
          val action= FeedFragmentDirections.actionFeedFragmentToYuklemeFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }else if(menu?.itemId==R.id.cikisItem){
            auth.signOut()
            val action=FeedFragmentDirections.actionFeedFragmentToKullaniciFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }
}