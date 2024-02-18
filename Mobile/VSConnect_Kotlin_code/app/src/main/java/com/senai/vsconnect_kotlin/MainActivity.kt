package com.senai.vsconnect_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.senai.vsconnect_kotlin.databinding.ActivityLoginBinding
import com.senai.vsconnect_kotlin.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {

    //É uma propriedade privada  como o nome binding do tipo ActivityLoginBinding
    private lateinit var binding: ContentMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        //Atribui à variável binding um objeto que contém referências (propriedades) aos elementos definidos no layout
        binding = ContentMainBinding.inflate(layoutInflater)


        binding.buttonPerfil.setOnClickListener(){
            val perfilIntent = Intent(this, PerfilActivity::class.java  )

            startActivity(perfilIntent)
        }

        binding.buttonSair.setOnClickListener(){
            val perfilIntent = Intent(this, LoginActivity::class.java  )

            startActivity(perfilIntent)
        }
        setContentView(binding.root)
    }


}