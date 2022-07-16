package com.wdretzer.bancodadosroom.recycler

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.util.strike


class ItensAdapterItensFinish(
    private val action: (InfoDados) -> Unit,
    private val action2: (InfoDados) -> Unit,
    private val action3: (InfoDados) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = AsyncListDiffer<InfoDados>(this, DIFF_UTIL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavouriteViewHolderItensFinish(
            inflater.inflate(R.layout.card_item_finish, parent, false), action, action2, action3
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavouriteViewHolderItensFinish -> holder.bind(diffUtil.currentList[position])
        }
    }

    override fun getItemCount(): Int = diffUtil.currentList.size

    fun updateList(newItens: MutableList<InfoDados>) {
        diffUtil.submitList(diffUtil.currentList.plus(newItens))
    }

    fun updateItem(item: InfoDados) {
        val newList =
            diffUtil.currentList.map { infoDados ->
                Log.d("Adapter MAp:", "$infoDados")
                if (infoDados.statusLembrete == item.statusLembrete) item
                else infoDados
            }
        diffUtil.submitList(newList)

        Log.d("Adapter:", "${diffUtil.currentList}")
        Log.d("InfoDados:", "$item")
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<InfoDados>() {
            override fun areItemsTheSame(oldItem: InfoDados, newItem: InfoDados): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: InfoDados, newItem: InfoDados): Boolean {
                return oldItem.tituloInfo == newItem.tituloInfo
            }
        }
    }
}


class FavouriteViewHolderItensFinish(
    view: View,
    private val action: (InfoDados) -> Unit,
    private val action2: (InfoDados) -> Unit,
    private val action3: (InfoDados) -> Unit,
) : RecyclerView.ViewHolder(view) {

    private val textItemTitulo: TextView = view.findViewById(R.id.titulo_card)
    private val textItemDescricao: TextView = view.findViewById(R.id.descricao_card)
    private val textItemData: TextView = view.findViewById(R.id.data_card)
    private val textItemTime: TextView = view.findViewById(R.id.time_card)

    private val delete: ImageView = view.findViewById(R.id.btn_delete)
    private val update: ImageView = view.findViewById(R.id.btn_update)
    private val alarmStatusImage: ImageView = view.findViewById(R.id.alarm_status_image)
    private val btnFinish: ShapeableImageView = view.findViewById(R.id.btn_finish)


    fun bind(itensList: InfoDados) {
        textItemTitulo.text = itensList.tituloInfo
        textItemDescricao.text = itensList.descricaoInfo
        textItemData.text = itensList.dataInfo
        textItemTime.text = itensList.horarioInfo
        alarmStatusImage.setImageResource(if (itensList.alarmStatusInfo) R.drawable.icon_alarm_on else R.drawable.icon_alarm_off)

        if (itensList.statusLembrete) btnFinish.visibility = View.INVISIBLE
        update.setOnClickListener { action.invoke(itensList) }
        delete.setOnClickListener { action2.invoke(itensList) }
        btnFinish.setOnClickListener { action3.invoke(itensList) }
    }
}
