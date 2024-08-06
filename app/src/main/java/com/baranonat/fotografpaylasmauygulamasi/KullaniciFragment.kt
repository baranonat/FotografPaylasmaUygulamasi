package com.baranonat.fotografpaylasmauygulamasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.baranonat.fotografpaylasmauygulamasi.databinding.FragmentKullaniciBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class KullaniciFragment : Fragment() {

    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.kayitButton.setOnClickListener { kaydet(it) }
        binding.girisButton.setOnClickListener { giris(it) }
        val guncellKullanici=auth.currentUser
        if(guncellKullanici!=null){
            val action= KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

    }

    fun kaydet(view:View){

        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {result->
            if(result.isSuccessful){
                val action= KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(action)
            }

        }.addOnFailureListener { exception->
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()

        }


    }

    fun giris(view: View){

        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()

        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { result->
            val action= KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(action)
        }.addOnFailureListener { exception->
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}