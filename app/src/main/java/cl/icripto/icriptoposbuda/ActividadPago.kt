package cl.icripto.icriptoposbuda

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
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

        findViewById<TextView>(R.id.MontoPagoValor).text = "$ $price"
        findViewById<TextView>(R.id.MonedaPagoValor).text = moneda
        findViewById<TextView>(R.id.MotivoPagoValor).text = "Pago lightning para $server, de $nombreLocal"

//        findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(getQrCodeBitmap("34234234j2l3kjrl23kj"))

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

                        val resp = response.body!!.string()
                        val invoice: String = JSONObject(resp).getString("encoded_payment_request")
                        val checkId = JSONObject(resp).getString("id")
                        val monedaCobro = JSONObject(resp).getString("currency")
                        val memo = "Pago de $${price.toInt()} $monedaCobro a $nombreLocal"
                        val satsAmount = JSONObject(resp).getString("amount")
                        Log.d("Respuesta", invoice)
                        Log.d("Respuesta", checkId)
                        Log.d("Respuesta", monedaCobro)
                        Log.d("Respuesta", memo)
                        Log.d("Respuesta", satsAmount)
                        Log.d("Respuesta",  "https://realtime.buda.com/sub?channel=lightninginvoices%40$checkId")

                        runOnUiThread {
                            findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                                getQrCodeBitmap(invoice)
                            )
//                        runOnUiThread {
//                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
//                            Toast.makeText(this@ActividadPago, "Invoice Pagado!", Toast.LENGTH_SHORT).show()
//                        }
                        }
                        runOnUiThread {
                            Log.d("Respuesta", "El URL encontro respuesta")
                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
                            Toast.makeText(
                                this@ActividadPago,
                                "Invoice Pagado!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
//                        val checkURL =
//                            "https://realtime.buda.com/sub?channel=lightninginvoices%40$checkId"
//                        val requestFinish = Request.Builder().url(checkURL).build()
//                        val clientFinish = OkHttpClient()
//                        clientFinish.newCall(requestFinish).enqueue(object : Callback {
//                            override fun onFailure(call: Call, e: IOException) {
//                                e.printStackTrace()
//                            }
//
//                            override fun onResponse(call: Call, response: Response) {
//                                response.use {
//                                    if (!response.isSuccessful) {
//                                        throw IOException("Unexpected code $response")
//                                    } else {
//
//                                        runOnUiThread {
//                                            Log.d("Respuesta", "El URL encontro respuesta")
//                                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
//                                            Toast.makeText(this@ActividadPago,"Invoice Pagado!", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
//                                }
//                            }
//                        })
                    }
                }
            }
        })

    }

    fun getQrCodeBitmap(invoice: String): Bitmap {
        val size = 240 //pixels
        hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(invoice, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}