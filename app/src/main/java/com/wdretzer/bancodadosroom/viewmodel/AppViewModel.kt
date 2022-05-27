package com.wdretzer.bancodadosroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wdretzer.bancodadosroom.dados.Dados
import com.wdretzer.bancodadosroom.repository.AppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class AppViewModel(
    private val repository: AppRepository = AppRepository.instance,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun addOrRemoveFavourite(item: String, item2: String) =
        repository.addOrRemoveFavourite(item, item2).flowOn(dispatcher).asLiveData()

    fun getFavourite() = repository.getFavourite().flowOn(dispatcher).asLiveData()

}
