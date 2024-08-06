package com.baranonat.fotografpaylasmauygulamasi.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.baranonat.fotografpaylasmauygulamasi.databinding.FragmentYuklemeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID


class YuklemeFragment : Fragment() {
    private var _binding: FragmentYuklemeBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel: Uri?=null
    private var secilenBitmap: Bitmap?=null
    private lateinit var storage:FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            registerLauncher()
        storage=Firebase.storage
        db=Firebase.firestore
        auth=Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYuklemeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageYukleme.setOnClickListener { gorselSec(it) }
        binding.yukleButton.setOnClickListener { yuklemeTiklandi(it) }
    }

fun yuklemeTiklandi(view: View){
val uuid=UUID.randomUUID()
    val gorselAdi="${uuid}.jpg"
    val reference=storage.reference

   val gorselReferans= reference.child("Images").child(gorselAdi)
    if(secilenGorsel!=null){
        gorselReferans.putFile(secilenGorsel!!).addOnSuccessListener { task->
            gorselReferans.downloadUrl.addOnSuccessListener { uri->
                val downloadUri=uri.toString()
        if(auth.currentUser!=null){
            val postMap=HashMap<String,Any>()
            postMap.put("downloadUrl",downloadUri)
            postMap.put("email",auth.currentUser!!.email.toString())
            postMap.put("comment",binding.commentText.text.toString())
            postMap.put("date",Timestamp.now())

            db.collection("Posts").add(postMap).addOnSuccessListener {documentReference->
                val action=YuklemeFragmentDirections.actionYuklemeFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(action)

            }.addOnFailureListener { exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }

            }
        }.addOnFailureListener{exception->

            Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }






}
    fun gorselSec(view:View){
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
           if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
               if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                   Snackbar.make(view,"Galeriye gitmemiz için izin ver",Snackbar.LENGTH_INDEFINITE).setAction(
                       "İzin Ver",
                       View.OnClickListener {
                           //izin iste
                           permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                       }
                   ).show()
               }else{
                   //izin iste
                   permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
               }
           }else{
               //galeriye git
               val intentToGalery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentToGalery)
           }
       } else{
           if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
               if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                   Snackbar.make(view,"Galeriye gitmemiz için izin ver",Snackbar.LENGTH_INDEFINITE).setAction(
                       "İzin Ver",
                       View.OnClickListener {
                           //izin iste
                           permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                       }
                   ).show()
               }else{
                   //izin iste
                   permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
               }
           }else{
               //galeriye git
               val intentToGalery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentToGalery)
           }
       }

    }

    fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->

            if(result.resultCode==RESULT_OK){
                val intent=result.data
                if(intent!=null){
                    secilenGorsel=intent.data
                    try{
                        if(Build.VERSION.SDK_INT>=28){
                            val source=ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            val bitmap=ImageDecoder.decodeBitmap(source)
                            binding.imageYukleme.setImageBitmap(bitmap)
                        }else{
                            val bitmap= MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageYukleme.setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        println(e.localizedMessage)
                    }
                }
            }

        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intentToGalery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }else{
                Toast.makeText(requireContext(),"İzin Verilmedi",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}