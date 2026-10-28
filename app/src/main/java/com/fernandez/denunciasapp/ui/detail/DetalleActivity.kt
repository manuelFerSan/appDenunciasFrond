package com.fernandez.denunciasapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.fernandez.denunciasapp.R
import com.fernandez.denunciasapp.databinding.ActivityDetalleBinding
import com.fernandez.denunciasapp.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.fernandez.denunciasapp.ui.viewmodel.DenunciaViewModel

class DetalleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleBinding
    private val viewModel: DenunciaViewModel by viewModels()
    private var denunciaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleBinding.inflate(layoutInflater)
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

        denunciaId = intent.getIntExtra("DENUNCIA_ID", -1)
        if (denunciaId == -1) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.detalle.observe(this) { d ->
            d ?: return@observe
            binding.tvTipo.text = d.tipo
            binding.tvId.text = "ID: ${d.id}"
            binding.tvDni.text = "DNI: ${d.dni}"
            binding.tvFecha.text = "Fecha: ${d.fecha ?: ""}"
            binding.tvDireccion.text = "Dirección: ${d.direccion}"
            binding.tvDescripcion.text = "Descripción: ${d.descripcion}"
            binding.tvEstado.text = d.estadoTexto()
            val statusColor = if (d.estado == 1) 0xFFF59E0B.toInt() else 0xFF16A34A.toInt()
            binding.tvEstado.backgroundTintList = android.content.res.ColorStateList.valueOf(statusColor)
            binding.btnAtender.isEnabled = d.estado == 1
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }

        binding.btnAtender.setOnClickListener {
            viewModel.atenderDenuncia(denunciaId) { ok, msg ->
                if (ok) {
                    binding.btnAtender.isEnabled = false
                    Snackbar.make(binding.root, "Denuncia marcada como atendida", Snackbar.LENGTH_SHORT).show()
                    binding.root.postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }, 1200)
                } else {
                    Toast.makeText(this, msg ?: "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.cargarDetalle(denunciaId)
    }
}