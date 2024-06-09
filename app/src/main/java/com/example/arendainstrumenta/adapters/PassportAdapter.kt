package com.example.arendainstrumenta.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.arendainstrumenta.R
import com.example.arendainstrumenta.data.Passport
import com.example.arendainstrumenta.databinding.PassportRvItemBinding

class PassportAdapter : RecyclerView.Adapter<PassportAdapter.PassportViewHolder>() {

    inner class PassportViewHolder(val binding: PassportRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(passport: Passport, isSelected: Boolean) {
            binding.apply {
                buttonPassport.text = passport.passportFIO
                if (isSelected) {
                    buttonPassport.background = ColorDrawable(itemView.context.resources.getColor(R.color.yellow))
                } else {
                    buttonPassport.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }
    }


    private val diffUtil = object : DiffUtil.ItemCallback<Passport>() {
        override fun areItemsTheSame(oldItem: Passport, newItem: Passport): Boolean {
            return oldItem.passportFIO == newItem.passportFIO && oldItem.vidan == newItem.vidan
        }

        override fun areContentsTheSame(oldItem: Passport, newItem: Passport): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassportViewHolder {
        return PassportViewHolder(
            PassportRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    var selectedPassport = -1
    override fun onBindViewHolder(holder: PassportViewHolder, position: Int) {
        val passport = differ.currentList[position]
        holder.bind(passport, selectedPassport == position)

        holder.binding.buttonPassport.setOnClickListener {
            if (selectedPassport >= 0)
                notifyItemChanged(selectedPassport)
            selectedPassport = holder.adapterPosition
            notifyItemChanged(selectedPassport)
            onClick?.invoke(passport)
        }
    }

    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedPassport)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    var onClick: ((Passport)-> Unit)? = null
}