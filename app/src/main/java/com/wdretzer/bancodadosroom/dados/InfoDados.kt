package com.wdretzer.bancodadosroom.dados

import com.google.firebase.Timestamp

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


data class InfoFirebaseFirestore(
    val tituloInfo: String,
    val descricaoInfo: String,
    val dataInfo: String,
    val horarioInfo: String,
    val alarmStatusInfo: Boolean,
    val idUser: Timestamp,
    val requestCode: Int,
    val statusLembrete: Boolean
)