package com.example.sshark

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sshark.ml.ModeloTiburonPrueba
import com.example.sshark.ml.RedAletas
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
        val model = RedAletas.newInstance(this)
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
        println("ooooooooooooooooooooooooooooooooooooooVALORooooooooooooooooooooooooooooooooooooooooo")
        println(valor)

        val stream = ByteArrayOutputStream()
        // Compress the bitmap with JPEG format and specified quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        db.collection("sSharkBase")
            .document(valor.toString())
            .get()
            .addOnSuccessListener { resultado ->
                val delim = "-"
                var veda = "NO"

                val cal = Calendar.getInstance()
                cal.add(Calendar.HOUR,-5)
                val instant = cal.time

                val year = cal[Calendar.YEAR]
                println(year)

                val veda_inicio_base = resultado["veda_inicio"].toString()
                val veda_fin_base = resultado["veda_fin"].toString()

                if (veda_inicio_base != "" && veda_fin_base != "") {

                    val veda_inicio_split = veda_inicio_base.split(delim)
                    val veda_inicio_dia = veda_inicio_split[0]
                    val veda_inicio_mes = veda_inicio_split[1]

                    val veda_fin_split = veda_fin_base.split(delim)
                    val veda_fin_dia = (veda_fin_split[0].toInt() + 1).toString()
                    val veda_fin_mes = veda_fin_split[1]

                    val veda_inicio_string = veda_inicio_dia + "/" + veda_inicio_mes + "/" + year
                    val veda_fin_string = veda_fin_dia + "/" + veda_fin_mes + "/" + year

                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    val veda_inicio = sdf.parse(veda_inicio_string)
                    val veda_fin = sdf.parse(veda_fin_string)

                    println(instant)
                    println(veda_inicio)
                    println(veda_fin)
                    println(instant.compareTo(veda_inicio))
                    println(instant.compareTo(veda_fin))

                    if (instant.compareTo(veda_inicio)>=0 && instant.compareTo(veda_fin)<=0) {
                        veda = "SI"
                    }
                }

                val intento1 = Intent(this, Respuesta::class.java)
                intento1.putExtra("nombre", resultado["nombre"].toString() );
                intento1.putExtra("protegido", resultado["protegido"].toString());
                intento1.putExtra("veda", veda);
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