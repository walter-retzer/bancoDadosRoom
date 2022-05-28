package com.wdretzer.bancodadosroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.wdretzer.bancodadosroom.repository.AppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


class AppViewModel(
    private val repository: AppRepository = AppRepository.instance,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun addOrRemoveItens(item: String, item2: String, item3: String, item4: String) =
        repository.addOrRemoveItens(item, item2, item3, item4).flowOn(dispatcher).asLiveData()

    fun getListSave() = repository.getListSave().flowOn(dispatcher).asLiveData()

}
