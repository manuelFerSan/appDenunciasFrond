package com.fernandez.denunciasapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.denunciasapp.data.model.Denuncia
import com.fernandez.denunciasapp.data.repository.DenunciaRepository
import kotlinx.coroutines.launch

class DenunciaViewModel : ViewModel() {

    private val repository = DenunciaRepository()

    private val _denuncias = MutableLiveData<List<Denuncia>>(emptyList())
    val denuncias: LiveData<List<Denuncia>> = _denuncias

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _detalle = MutableLiveData<Denuncia?>(null)
    val detalle: LiveData<Denuncia?> = _detalle

    private val _hasMore = MutableLiveData(true)
    val hasMore: LiveData<Boolean> = _hasMore

    private var offset = 0
    private val limit = 5

    fun cargarDenuncias(reset: Boolean = false) {
        if (_loading.value == true) return
        if (reset) {
            offset = 0
            _denuncias.value = emptyList()
            _hasMore.value = true
        }
        if (_hasMore.value == false && !reset) return

        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            repository.getDenuncias(offset, limit)
                .onSuccess { lista ->
                    if (lista.isEmpty()) {
                        _hasMore.value = false
                    } else {
                        val existentes = _denuncias.value.orEmpty().mapNotNull { it.id }.toSet()
                        val nuevas = lista.filter { it.id == null || it.id !in existentes }
                        val actual = _denuncias.value.orEmpty()
                        _denuncias.value = actual + nuevas
                        offset += lista.size
                        _hasMore.value = lista.size >= limit
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _loading.value = false
        }
    }

    fun cargarMas() = cargarDenuncias(false)

    fun buscarPorDni(dni: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            repository.getDenunciasPorCiudadano(dni)
                .onSuccess { lista ->
                    _denuncias.value = lista
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _loading.value = false
        }
    }

    fun cargarDetalle(id: Int) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            repository.getDenuncia(id)
                .onSuccess { denuncia ->
                    _detalle.value = denuncia
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _loading.value = false
        }
    }

    fun registrarDenuncia(denuncia: Denuncia, onResult: (Boolean, String?) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            repository.registrarDenuncia(denuncia)
                .onSuccess {
                    onResult(true, null)
                }
                .onFailure { e ->
                    onResult(false, e.message)
                }
            _loading.value = false
        }
    }

    fun atenderDenuncia(id: Int, onResult: (Boolean, String?) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            repository.atenderDenuncia(id)
                .onSuccess { denuncia ->
                    _detalle.value = denuncia
                    onResult(true, null)
                }
                .onFailure { e ->
                    onResult(false, e.message)
                }
            _loading.value = false
        }
    }
}