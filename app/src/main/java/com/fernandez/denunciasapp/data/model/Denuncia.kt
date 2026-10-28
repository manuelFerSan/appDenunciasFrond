package com.fernandez.denunciasapp.data.model

import com.google.gson.annotations.SerializedName

data class Denuncia(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("dni") val dni: String,
    @SerializedName("fecha") val fecha: String? = null,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("estado") val estado: Int = 1
) {
    fun estadoTexto(): String = if (estado == 1) "Pendiente" else "Atendida"
}