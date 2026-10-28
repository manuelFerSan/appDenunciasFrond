package com.fernandez.denunciasapp.ui.form

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
import com.fernandez.denunciasapp.data.model.Denuncia
import com.fernandez.denunciasapp.databinding.ActivityRegistroBinding
import com.fernandez.denunciasapp.ui.main.MainActivity
import com.fernandez.denunciasapp.ui.viewmodel.DenunciaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private val viewModel: DenunciaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
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

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnGuardar.isEnabled = !isLoading
        }

        binding.btnGuardar.setOnClickListener {
            val dni = binding.etDni.text.toString().trim()
            val tipo = binding.etTipo.text.toString().trim()
            val direccion = binding.etDireccion.text.toString().trim()
            val descripcion = binding.etDescripcion.text.toString().trim()

            if (dni.length != 8 || tipo.isEmpty() || direccion.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

            val denuncia = Denuncia(
                dni = dni,
                fecha = fecha,
                tipo = tipo,
                direccion = direccion,
                descripcion = descripcion,
                estado = 1
            )

            viewModel.registrarDenuncia(denuncia) { ok, msg ->
                if (ok) {
                    Toast.makeText(this, "Denuncia registrada", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, msg ?: "Error al registrar", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}