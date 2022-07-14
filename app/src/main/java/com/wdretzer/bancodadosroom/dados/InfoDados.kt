package com.wdretzer.bancodadosroom.dados

data class InfoDados(
    val tituloInfo: String,
    val descricaoInfo: String,
    val dataInfo: String,
    val horarioInfo: String,
    val alarmStatusInfo: Boolean,
    val idUser: Int,
    val requestCode: Int,
    val statusLembrete: Boolean
)
