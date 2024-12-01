package com.example.delivery_app_kotlin.delivery.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery_app_kotlin.auth.model.entities.UserModel
import com.example.delivery_app_kotlin.databinding.DgItemListBinding
import com.example.delivery_app_kotlin.delivery.viewmodel.DeliveryViewModel

class NewDeliveryAdapter(private val deliveryViewModel: DeliveryViewModel) :
    RecyclerView.Adapter<NewDeliveryAdapter.NewDeliveryViewHolder>() {

    class NewDeliveryViewHolder(val itemBinding: DgItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.userId == newItem.userId &&
                    oldItem.email == newItem.email &&
                    oldItem.userType == newItem.userType
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDeliveryViewHolder {
        return NewDeliveryViewHolder(
            DgItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            ActivityNewDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            ActivityNewDeliveryBinding.inflate(R.layout.dg_item_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewDeliveryViewHolder, position: Int) {
        val selected = differ.currentList[position]
        holder.itemBinding.titleText.text =
            if (deliveryViewModel.selectedDG.value?.userId == selected.userId) "${selected.name} (Selected)" else selected.name
//        holder.itemBinding.itemListLayout.setBackgroundColor(Color.TRANSPARENT)
        holder.itemBinding.itemListLayout.setOnClickListener {
            deliveryViewModel.setSelectedDG(selected)

//            val color = ContextCompat.getColor(holder.itemBinding.root.context, R.color.purple_200)
//            holder.itemBinding.itemListLayout.setBackgroundColor(color)
            holder.itemBinding.titleText.text = "${selected.name} (Selected)"
//            notifyDataSetChanged()
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}