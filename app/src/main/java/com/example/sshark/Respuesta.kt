package com.example.sshark

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Respuesta : AppCompatActivity() {

    private lateinit var imagenTiburon : ImageView
    private lateinit var textNombre : TextView
    private lateinit var textProtegido : TextView
    private lateinit var textVeda : TextView
    private lateinit var buttonContinuar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respuesta)

        imagenTiburon = findViewById(R.id.imageView2)
        textNombre = findViewById(R.id.textView)
        textProtegido = findViewById(R.id.textView4)
        textVeda = findViewById(R.id.textView7)
        buttonContinuar = findViewById(R.id.button)

        textNombre.apply {
            text = intent.getStringExtra("nombre")
        }
        textProtegido.apply {
            text = intent.getStringExtra("protegido")
            if(text == "NO"){
                textProtegido.setTextColor(Color.parseColor("#F52020"));
            } else{
                textProtegido.setTextColor(Color.parseColor("#56CF25"));
            }
        }
        textVeda.apply {
            text = intent.getStringExtra("veda")
            if(text == "NO"){
                textVeda.setTextColor(Color.parseColor("#F52020"));
            } else{
                textVeda.setTextColor(Color.parseColor("#56CF25"));
            }
        }


        val extras = intent.extras
        val byteArray = extras!!.getByteArray("imagen")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        imagenTiburon.setImageBitmap(bmp)

        buttonContinuar.setOnClickListener {
            val intento = Intent(this, MainActivity::class.java)
            startActivity(intento)
        }

    }

    override fun onBackPressed() {
        val intento = Intent(this, MainActivity::class.java)
        startActivity(intento)
    }

}