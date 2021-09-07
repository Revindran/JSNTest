package com.raveendran.jsntest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.raveendran.jsntest.databinding.RowPeopleLayoutBinding
import com.raveendran.jsntest.model.People

class PeopleAdapter : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    inner class PeopleViewHolder(val binding: RowPeopleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<People>() {
        override fun areItemsTheSame(oldItem: People, newItem: People): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: People, newItem: People): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return PeopleViewHolder(
            RowPeopleLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val ppl = differ.currentList[position]
        holder.binding.apply {
            Glide.with(this.root).load(ppl.avatar).diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView)
            nameTv.text = "Name : ${ppl.first_name} ${ppl.last_name}"
            emailTv.text = "Email : ${ppl.email}"
            this.root.setOnClickListener {
                onItemClickListener?.let { it(ppl) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((People) -> Unit)? = null

    fun setOnItemClickListener(listener: (People) -> Unit) {
        onItemClickListener = listener
    }


}