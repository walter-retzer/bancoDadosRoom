package com.wdretzer.bancodadosroom.repository

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wdretzer.bancodadosroom.bd.AppRoomEntity
import com.wdretzer.bancodadosroom.bd.DataBaseFactory
import com.wdretzer.bancodadosroom.dados.Dados
import com.wdretzer.bancodadosroom.dados.InfoDados
import com.wdretzer.bancodadosroom.extension.DataResult
import com.wdretzer.bancodadosroom.extension.updateStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AppRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val dao = DataBaseFactory.getDataBase().appRoomDao()


    fun addOrRemoveFavourite(item: String, item2: String) = flow {
        try {
            val word = Dados(0, item, item2)
            //Insere a lista no Banco de Dados:
            val insert = dao.insert(word)
            emit(DataResult.Success(insert))

        } catch (e: Exception) {
            emit(DataResult.Error(IllegalStateException()))
        }
    }.updateStatus().flowOn(dispatcher)


    //Função para pegar os dados do item Favoritado:
    fun getFavourite() = flow<MutableList<InfoDados>> {
        val localItens = dao.listAll().map {
            InfoDados(it.titulo, it.horario)
        }
        emit((localItens as MutableList<InfoDados>))
    }.flowOn(dispatcher)

    companion object {
        val instance: AppRepository by lazy { AppRepository() }
    }

}