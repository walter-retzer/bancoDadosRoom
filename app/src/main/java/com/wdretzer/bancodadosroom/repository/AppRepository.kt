package com.wdretzer.bancodadosroom.repository

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

    //Insere os valores dos itens que foram digitados na tela Inicial ao BD:
    fun addOrRemoveItens(item: String, item2: String, item3: String, item4: String) = flow {
        try {
            val word = Dados(null, item, item2, item3, item4)
            val insert = dao.insert(word)
            emit(DataResult.Success(insert))
        } catch (e: Exception) {
            emit(DataResult.Error(IllegalStateException()))
        }
    }.updateStatus().flowOn(dispatcher)


    //Função para pegar a lista de Dados salva no BD:
    fun getListSave() = flow {
        val localItens = dao.listAll().map {
            InfoDados(it.titulo, it.descricao, it.data, it.horario, it.id!!)
        }
        emit((localItens as MutableList<InfoDados>))
    }.flowOn(dispatcher)


    //Função para pegar a Lista dos dados dos lembretes do dia atual:
    fun listItensToday(item: String) = flow {
        val localItens = dao.listItensToday(item).map {
            InfoDados(it.titulo, it.descricao, it.data, it.horario, it.id!!)
        }
        emit((localItens as MutableList<InfoDados>))
    }.flowOn(dispatcher)


    //Executa a contagem de item no banco de dados que contêm a mesma data:
    fun countItens(item: String) = flow {
        try {
            val numeroRegistro = dao.countApiId(item)
            val itemExist = numeroRegistro >= 1
            emit(numeroRegistro)
        } catch (e: Exception) {
            emit(IllegalStateException())
        }
    }.flowOn(dispatcher)


    //Função para editar os campos dos itens inseridos no BD:
    fun updateItem(item: InfoDados) = flow {
        val delete = dao.updateAll(
            item.idUser,
            item.tituloInfo,
            item.descricaoInfo,
            item.dataInfo,
            item.horarioInfo
        )
        emit(DataResult.Success(delete))
    }.updateStatus().flowOn(dispatcher)


    //Função para deletar um item especifico no BD:
    fun deleteItem(item: InfoDados) = flow {
        val delete = dao.deleteByApiId(
            item.idUser
        )
        emit(DataResult.Success(delete))
    }.updateStatus().flowOn(dispatcher)


    //Função para deletar todos os dados no BD:
    fun deleteAll() = flow {
        val delete = dao.deleteAll()
        emit(DataResult.Success(delete))
    }.updateStatus().flowOn(dispatcher)


    companion object {
        val instance: AppRepository by lazy { AppRepository() }
    }
}
