package cl.icripto.icriptoposbuda

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ActividadPago : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividad_pago)
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val defaultPrice = 0.0
        val defaultNombreLocal = ""
        val defaultServer = ""
        val defaultmoneda = "CLP"

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val price = sharedPreferences.getString("PRICE", defaultPrice.toString()).toString().toDouble()
        val nombreLocal = sharedPreferences.getString("LOCALNOMBRE", defaultNombreLocal).toString()
        val server = sharedPreferences.getString("LOCALSERVER", defaultServer).toString()
        val moneda = sharedPreferences.getString("LOCALMONEDA", defaultmoneda).toString()

        val urlBuda = "https://www.buda.com/api/v2/pay/${server}/invoice?amount=${price}&description=cobro_${nombreLocal}"

        findViewById<TextView>(R.id.MontoPagoValor).setText("$ $price")
        findViewById<TextView>(R.id.MonedaPagoValor).setText(moneda)
        findViewById<TextView>(R.id.MotivoPagoValor).setText("Pago lightning para $server, de $nombreLocal")



        val request = Request.Builder()
            .url(urlBuda)
            .build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful)
                    {
                        throw IOException("Unexpected code $response")
                    } else {

                        val response = response.body!!.string()
                        val invoice = JSONObject(response).getString("encoded_payment_request")
                        val checkId = JSONObject(response).getString("id")
                        val monedaCobro = JSONObject(response).getString("currency")
                        val memo = "Pago de $${price.toInt()} $monedaCobro a $nombreLocal"
                        val satsAmount = JSONObject(response).getString("amount")
                        Log.d("Respuesta", invoice)
                        Log.d("Respuesta", checkId)
                        Log.d("Respuesta", monedaCobro)
                        Log.d("Respuesta", memo)
                        Log.d("Respuesta", satsAmount)





                    }
                }
            }
        })

    }
}