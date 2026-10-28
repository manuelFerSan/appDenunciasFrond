package com.fernandez.denunciasapp.data.network

import com.fernandez.denunciasapp.data.model.Denuncia
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Registrar denuncia (estado inicia en 1)
    @POST("denuncias/")
    suspend fun registrarDenuncia(@Body denuncia: Denuncia): Response<Denuncia>

    // Obtener denuncias paginadas
    @GET("denuncias/")
    suspend fun getDenunciasPaginadas(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<ResponseBody>

    // Obtener denuncia por id
    @GET("denuncias/{id}")
    suspend fun getDenuncia(@Path("id") id: Int): Response<Denuncia>

    // Obtener denuncias de un ciudadano por DNI
    @GET("denuncias/ciudadano/{dni}")
    suspend fun getDenunciasPorCiudadano(@Path("dni") dni: String): Response<ResponseBody>

    // Atender denuncia (cambia estado de 1 a 0)
    @PUT("denuncias/{id}")
    suspend fun atenderDenuncia(@Path("id") id: Int): Response<Denuncia>
}