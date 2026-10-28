package com.fernandez.denunciasapp.ui.search

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
import com.fernandez.denunciasapp.databinding.ActivityBusquedaBinding
import com.fernandez.denunciasapp.ui.adapter.DenunciaAdapter
import com.fernandez.denunciasapp.ui.detail.DetalleActivity
import com.fernandez.denunciasapp.ui.main.MainActivity
import com.fernandez.denunciasapp.ui.viewmodel.DenunciaViewModel

class BusquedaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusquedaBinding
    private val viewModel: DenunciaViewModel by viewModels()
    private lateinit var adapter: DenunciaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaBinding.inflate(layoutInflater)
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

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.inflateMenu(R.menu.menu_toolbar)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_home) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
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
            adapter.submitList(lista)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }

        binding.btnBuscar.setOnClickListener {
            val dni = binding.etDni.text.toString().trim()
            if (dni.length != 8) {
                Toast.makeText(this, "Ingrese un DNI válido de 8 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.buscarPorDni(dni)
        }
    }
}