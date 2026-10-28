package com.fernandez.denunciasapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fernandez.denunciasapp.R
import com.fernandez.denunciasapp.databinding.ActivityMainBinding
import com.fernandez.denunciasapp.ui.adapter.DenunciaAdapter
import com.fernandez.denunciasapp.ui.detail.DetalleActivity
import com.fernandez.denunciasapp.ui.form.RegistroActivity
import com.fernandez.denunciasapp.ui.search.BusquedaActivity
import com.fernandez.denunciasapp.ui.viewmodel.DenunciaViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DenunciaViewModel by viewModels()
    private lateinit var adapter: DenunciaAdapter
    private var filtroEstado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.statusBarSpacer.layoutParams.height = bars.top
            binding.statusBarSpacer.requestLayout()
            view.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        binding.toolbar.inflateMenu(R.menu.menu_home)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_refresh) {
                viewModel.cargarDenuncias(reset = true)
                true
            } else false
        }

        adapter = DenunciaAdapter { denuncia ->
            denuncia.id?.let { id ->
                val intent = Intent(this, DetalleActivity::class.java)
                intent.putExtra("DENUNCIA_ID", id)
                startActivity(intent)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.denuncias.observe(this) { lista ->
            aplicarFiltro(lista.orEmpty())
        }

        viewModel.hasMore.observe(this) { more ->
            binding.btnCargarMas.isEnabled = more == true
            binding.btnCargarMas.alpha = if (more == true) 1f else 0.5f
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }

        binding.btnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        binding.btnBuscar.setOnClickListener {
            startActivity(Intent(this, BusquedaActivity::class.java))
        }

        binding.btnFiltroTodos.setOnClickListener {
            filtroEstado = null
            actualizarFiltroUI()
            aplicarFiltro(viewModel.denuncias.value.orEmpty())
        }
        binding.btnFiltroPendientes.setOnClickListener {
            filtroEstado = 1
            actualizarFiltroUI()
            aplicarFiltro(viewModel.denuncias.value.orEmpty())
        }
        binding.btnFiltroAtendidas.setOnClickListener {
            filtroEstado = 0
            actualizarFiltroUI()
            aplicarFiltro(viewModel.denuncias.value.orEmpty())
        }

        actualizarFiltroUI()

        binding.btnCargarMas.setOnClickListener {
            viewModel.cargarMas()
        }

        viewModel.cargarDenuncias(reset = true)
    }

    private fun actualizarFiltroUI() {
        val primary = android.content.res.ColorStateList.valueOf(0xFF2563EB.toInt())
        val white = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
        val surface = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())

        val botones = listOf(
            binding.btnFiltroTodos to null,
            binding.btnFiltroPendientes to 1,
            binding.btnFiltroAtendidas to 0
        )
        botones.forEach { (btn, estado) ->
            val seleccionado = estado == filtroEstado
            btn.backgroundTintList = if (seleccionado) primary else surface
            btn.setTextColor(if (seleccionado) 0xFFFFFFFF.toInt() else 0xFF2563EB.toInt())
        }
    }

    private fun aplicarFiltro(lista: List<com.fernandez.denunciasapp.data.model.Denuncia>) {
        val filtrada = when (filtroEstado) {
            null -> lista
            else -> lista.filter { it.estado == filtroEstado }
        }
        adapter.submitList(filtrada)
        binding.tvEmpty.visibility = if (filtrada.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        // Recargar lista al volver de otras pantallas
        viewModel.cargarDenuncias(reset = true)
    }
}