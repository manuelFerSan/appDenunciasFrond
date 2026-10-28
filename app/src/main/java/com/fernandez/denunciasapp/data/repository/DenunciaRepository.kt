package com.fernandez.denunciasapp.data.repository

import com.fernandez.denunciasapp.data.model.Denuncia
import com.fernandez.denunciasapp.data.network.ApiClient
import com.fernandez.denunciasapp.data.network.JsonParser

class DenunciaRepository {

    private val api = ApiClient.apiService

    suspend fun getDenuncias(offset: Int, limit: Int): Result<List<Denuncia>> = runCatching {
        val response = api.getDenunciasPaginadas(offset, limit)
        if (response.isSuccessful) {
            JsonParser.parseDenuncias(response.body())
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDenuncia(id: Int): Result<Denuncia> = runCatching {
        val response = api.getDenuncia(id)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Respuesta vacía")
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getDenunciasPorCiudadano(dni: String): Result<List<Denuncia>> = runCatching {
        val response = api.getDenunciasPorCiudadano(dni)
        if (response.isSuccessful) {
            JsonParser.parseDenuncias(response.body())
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    suspend fun registrarDenuncia(denuncia: Denuncia): Result<Denuncia> = runCatching {
        val response = api.registrarDenuncia(denuncia)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Respuesta vacía")
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    suspend fun atenderDenuncia(id: Int): Result<Denuncia> = runCatching {
        val response = api.atenderDenuncia(id)
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Respuesta vacía")
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }
}