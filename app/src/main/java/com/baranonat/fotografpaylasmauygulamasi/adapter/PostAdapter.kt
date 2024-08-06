package com.baranonat.fotografpaylasmauygulamasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baranonat.fotografpaylasmauygulamasi.databinding.RecyclerRowBinding
import com.baranonat.fotografpaylasmauygulamasi.model.Posts
import com.squareup.picasso.Picasso

class PostAdapter(val postListesi:ArrayList<Posts>):RecyclerView.Adapter<PostAdapter.PostHolder>() {
    class PostHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
       return PostHolder(binding)
    }

    override fun getItemCount(): Int {
       return postListesi.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
       holder.binding.recyclerEmailText.text=postListesi[position].email
        holder.binding.recyclercommentText.text=postListesi[position].comment
        Picasso.get().load(postListesi[position].downloadUrl).into(holder.binding.recyclerImageView)
    }
}