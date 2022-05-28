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

    fun addOrRemoveItens(item: String, item2: String, item3: String, item4: String) = flow {
        try {
            // Executa a contagem de item no banco de dados igual ao valor da variavel item:
            val numeroRegistro = dao.countApiId(item)
            val itemExist = numeroRegistro >= 1

            var word = Dados(null, item, item2, item3, item4)
            var word1 = Dados(null, item, item2, item3, numeroRegistro.toString())

            if (item == "del") {
                // Delete apenas se o item titulo tiver o valor "Reunião"
                val delete = dao.deleteByApiId("Nova Reunião")
                emit(DataResult.Success(delete))

            } else if (item == "ww") {
                //Update (atualização dos itens contidos no id: 0)
                val delete = dao.updateAll(
                    0,
                    "Nova Reunião",
                    "Assuntos Estratégicos em Pauta",
                    "22/06/2022",
                    "12h00"
                )
                emit(DataResult.Success(delete))

            } else if (item == "delete") {
                //Deleta todos os dados do Banco de Dados
                val delete = dao.deleteAll()
                emit(DataResult.Success(delete))

            } else {
                //Insere os valores dos itens que foram digitados na tela Inicial:
                val insert = dao.insert(word)
                emit(DataResult.Success(insert))
            }

        } catch (e: Exception) {
            emit(DataResult.Error(IllegalStateException()))
        }
    }.updateStatus().flowOn(dispatcher)


    //Função para pegar os dados do item Favoritado:
    fun getListSave() = flow<MutableList<InfoDados>> {
        val localItens = dao.listAll().map {
            InfoDados(it.titulo, it.descricao, it.data, it.horario)
        }

        emit((localItens as MutableList<InfoDados>))
    }.flowOn(dispatcher)


    companion object {
        val instance: AppRepository by lazy { AppRepository() }
    }
}
