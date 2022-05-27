package com.wdretzer.bancodadosroom.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.dados.Dados
import com.wdretzer.bancodadosroom.dados.InfoDados


class ItensAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = AsyncListDiffer<InfoDados>(this, DIFF_UTIL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavouriteViewHolder(
            inflater.inflate(R.layout.card_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavouriteViewHolder -> holder.bind(diffUtil.currentList[position])
        }
    }

    override fun getItemCount(): Int = diffUtil.currentList.size

    fun updateList(newItens: MutableList<InfoDados>) {
        diffUtil.submitList(diffUtil.currentList.plus(newItens))
    }

    fun updateItem(item: InfoDados) {
//        val newList =
//            diffUtil.currentList.map { nasa ->
//                if (nasa.href == item.href) item
//                else nasa
//            }
//        diffUtil.submitList(newList)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<InfoDados>() {
            override fun areItemsTheSame(oldItem: InfoDados, newItem: InfoDados): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: InfoDados, newItem: InfoDados): Boolean {
                return oldItem.info == newItem.info
            }
        }
    }
}


class FavouriteViewHolder(
    view: View,
) : RecyclerView.ViewHolder(view) {

    private val textFav: TextView = view.findViewById(R.id.text)
    private val textFav2: TextView = view.findViewById(R.id.text2)

    fun bind(itensFav: InfoDados) {
        textFav.text = itensFav.info
        textFav2.text = itensFav.info2
    }
}
