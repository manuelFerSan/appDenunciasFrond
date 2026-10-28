package com.fernandez.denunciasapp.data.network

import com.fernandez.denunciasapp.data.model.Denuncia
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

object JsonParser {

    private val gson = Gson()

    fun parseDenuncias(body: ResponseBody?): List<Denuncia> {
        if (body == null) return emptyList()
        val raw = body.string().trim()
        if (raw.isEmpty()) return emptyList()
        return try {
            if (raw.startsWith("[")) {
                val type = object : TypeToken<List<Denuncia>>() {}.type
                gson.fromJson(raw, type) ?: emptyList()
            } else {
                // Puede ser un objeto Page de Spring: { "content": [ ... ] }
                val obj = gson.fromJson(raw, Map::class.java) as? Map<*, *>
                val content = obj?.get("content")
                val json = gson.toJson(content)
                val type = object : TypeToken<List<Denuncia>>() {}.type
                gson.fromJson<List<Denuncia>>(json, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}