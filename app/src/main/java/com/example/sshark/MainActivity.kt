package com.example.sshark

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.example.sshark.ml.ModeloTiburonPrueba
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var buttonDorsal : ImageButton
    private lateinit var buttonPectoral : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_principal)

        buttonDorsal = findViewById(R.id.imageButton6)
        buttonPectoral = findViewById(R.id.imageButton7)

        buttonDorsal.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intento1 = Intent(this, Espera::class.java)
                startActivity(intento1)
                //takePicturePreview.launch(null)
            } else {
                requestPermission.launch(Manifest.permission.CAMERA)
            }

            //val intento = Intent(this, Principal::class.java)
            //startActivity(intento)
        }

        buttonPectoral.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intento1 = Intent(this, Espera::class.java)
                startActivity(intento1)
                //takePicturePreview.launch(null)
            } else {
                requestPermission.launch(Manifest.permission.CAMERA)
            }

            //val intento = Intent(this, Principal::class.java)
            //startActivity(intento)
        }
    }
    //request camera permission
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted->
        if(granted){
            val intento1 = Intent(this, Espera::class.java)
            startActivity(intento1)
            //takePicturePreview.launch(null)
        }else {
            Toast.makeText(this, "Permiso denegado!! Intentalo denuevo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

}