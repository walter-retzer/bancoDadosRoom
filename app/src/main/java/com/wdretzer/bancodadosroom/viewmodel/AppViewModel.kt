package com.wdretzer.bancodadosroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.repository.AppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class AppViewModel(
    private val repository: AppRepository = AppRepository.instance,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun addItens(
        titulo: String,
        descricaao: String,
        data: String,
        horario: String,
        alarme: Boolean,
        requestCode: Int,
        statuslembrete: Boolean
    ) =
        repository.addItens(
            titulo,
            descricaao,
            data,
            horario,
            alarme,
            requestCode,
            statuslembrete
        ).flowOn(dispatcher).asLiveData()

    fun getListSave() = repository.getListSave().flowOn(dispatcher).asLiveData()

    fun updateItem(item: InfoDados) = repository.updateItem(item).flowOn(dispatcher).asLiveData()

    fun deleteItem(item: InfoDados) = repository.deleteItem(item).flowOn(dispatcher).asLiveData()

    fun deleteAll() = repository.deleteAll().flowOn(dispatcher).asLiveData()

    fun countItens(data: String) = repository.countItens(data).flowOn(dispatcher).asLiveData()

    fun countItensTime(time: String) = repository.countItensTime(time).flowOn(dispatcher).asLiveData()

    fun countAllItensFinish(status: Boolean) = repository.countAllItensFinish(status).flowOn(dispatcher).asLiveData()

    fun countItensFinish(data:String, time: Boolean) = repository.countItensFinish(data, time).flowOn(dispatcher).asLiveData()

    fun listItensToday(item: String) = repository.listItensToday(item).flowOn(dispatcher).asLiveData()

    fun listItensItensFinish(statusLembrete: Boolean) = repository.listItensItensFinish(statusLembrete).flowOn(dispatcher).asLiveData()

    fun listItensTodayFinish(date: String, statusLembrete: Boolean) = repository.listItensTodayFinish(date, statusLembrete).flowOn(dispatcher).asLiveData()


}
