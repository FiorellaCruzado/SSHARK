package com.example.sshark

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sshark.ml.ModeloTiburonPrueba
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Espera : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espera)
        takePicturePreview.launch(null)
    }

    //launch camera and take picture
    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap->
        if(bitmap != null){
            outputGenerator(bitmap)
        }
    }

    private fun outputGenerator(bitmap: Bitmap){
        //declearing tensor flow lite model variable
        val model = ModeloTiburonPrueba.newInstance(this)
        //val model = BirdsModel.newInstance(this)

        // converting bitmap into tensor flow image
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val tfimage = TensorImage.fromBitmap(newBitmap)

        //process the image using trained model and sort it in descending order
        val outputs = model.process(tfimage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }

        //getting result having high probability
        val highProbabilityOutput = outputs[0]

        //setting output text
        //tvOutput.text = highProbabilityOutput.label
        //inputprueba.setText(highProbabilityOutput.label)
        Log.i("TAG", "outputGenerator: $highProbabilityOutput")

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        var valor = highProbabilityOutput.label
        println("ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo")
        println(valor)
        val stream = ByteArrayOutputStream()
        // Compress the bitmap with JPEG format and specified quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        println(byteArray)

        db.collection("sSharkBase")
            .document(valor.toString())
            .get()
            .addOnSuccessListener { resultado ->
                val delim = "-"
                val instant = Date()
                val cal = Calendar.getInstance()
                val year = cal[Calendar.YEAR]

                val veda_inicio = resultado["veda_inicio"].toString()
                val veda_inicio_split = veda_inicio.split(delim)
                val veda_inicio_dia = veda_inicio_split[0]
                val veda_inicio_mes = veda_inicio_split[1]

                val veda_fin = resultado["veda_fin"].toString()
                val veda_fin_split = veda_fin.split(delim)
                val veda_fin_dia = veda_fin_split[0]
                val veda_fin_mes = veda_fin_split[1]


                //val veda = new Date.of(year,veda_inicio_mes,veda_inicio_dia)


                val dateFormat = SimpleDateFormat("d MMM yyyy, EEE, HH:mm:ss z")
                val date = dateFormat.format(Date())

                val intento1 = Intent(this, Respuesta::class.java)
                intento1.putExtra("nombre", resultado["nombre"].toString() );
                intento1.putExtra("protegido", resultado["protegido"].toString());
                intento1.putExtra("veda", year.toString() );
                intento1.putExtra("imagen",byteArray)
                startActivity(intento1)
            }
            .addOnFailureListener{ exception ->
                val intento2 = Intent(this, Respuesta::class.java)
                intento2.putExtra("nombre", "No se ha podido conectar");
                startActivity(intento2)
            }

    }
}