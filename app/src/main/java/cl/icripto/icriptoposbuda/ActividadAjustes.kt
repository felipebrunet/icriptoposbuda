package cl.icripto.icriptoposbuda

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible


class ActividadAjustes : AppCompatActivity() {

//    Main function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividad_ajustes)

//    Make current server settings selectable for copy by the user

//        val switchInternet = findViewById<View>(R.id.onChain) as Switch
//        switchInternet.isClickable = false
        findViewById<TextView>(R.id.servidorActualValor).setTextIsSelectable(true)
        findViewById<TextView>(R.id.IDActualValor).setTextIsSelectable(true)

//    Github Project URL. Make it selectable by user for opening it in the browser
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

//    Main predefined values: Currency, Merchant Name, Server URL, Store ID (string generated by the BTCPay server instance)
        val defaultMoneda = "CLP"
        val defaultLocal = "Restaurant A"
        val defaultServer = ""
        val defaultStoreId = ""
        val defaultTips = "no"

//    Loading current saved parameters. Same parameters from above: Currency, Merchant Name, Server URL, Store ID
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val savedLocal: String? = sharedPreferences.getString("LOCALNOMBRE", defaultLocal)
        val savedMoneda: String? = sharedPreferences.getString("LOCALMONEDA", defaultMoneda)
        val savedServer: String? = sharedPreferences.getString("LOCALSERVER", defaultServer)
        val savedID: String? = sharedPreferences.getString("LOCALID", defaultStoreId)
        val tips : String? = sharedPreferences.getString("STATUSTIPS", defaultTips)


        findViewById<View>(R.id.onChain).isInvisible = true
        findViewById<TextView>(R.id.TextoIDTienda).isInvisible = true
        findViewById<EditText>(R.id.IDTienda).isInvisible = true
        findViewById<TextView>(R.id.IDActual).isInvisible = true
        findViewById<TextView>(R.id.IDActualValor).isInvisible = true

//    Filling displayed server data (bottom of screen) with server parameters.
        findViewById<TextView>(R.id.servidorActualValor).text = savedServer
//        findViewById<TextView>(R.id.IDActualValor).text = savedID

//    Filling parameter text fields with server parameters and merchant name
        findViewById<EditText>(R.id.NLocal).setText(savedLocal)
        findViewById<EditText>(R.id.URLServicio).setText(savedServer)
        findViewById<EditText>(R.id.IDTienda).setText(savedID)
        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"

//    Setup the dropdown options for currency selection
        val option : Spinner = findViewById(R.id.spinner_currencies2)
        val options : Array<String>
        if (savedMoneda == null) {
            options = arrayOf("ARS", "CLP", "COP", "PEN")
        } else {
            options = arrayOf(savedMoneda) + arrayOf("ARS", "CLP", "COP", "PEN").filter{s -> s != savedMoneda}
        }
        var moneda : String = savedMoneda.toString()


//    Setup of the "Go Back" button
        val volverButton = findViewById<Button>(R.id.botonVolver)
        volverButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

//    Logic for dropdown selector of currency
        option.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                moneda = options[p2]
            }
        }

        val resetPinButton = findViewById<Button>(R.id.buttonClearPin)
        resetPinButton.setOnClickListener {
            val sharedPreferencesPin : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferencesPin.edit()
            editor.apply{
                putString("LOCALPIN", "")
            }.apply()
            Toast.makeText(this, "PIN Borrado", Toast.LENGTH_SHORT).show()

        }

//    Setup of the "Save" button
        val guardarButton = findViewById<Button>(R.id.botonGuardar)
        guardarButton.setOnClickListener {
            openMainActivitySaved(moneda)
        }

    }


    private fun openMainActivitySaved(moneda : String) {
        val intent = Intent(this, MainActivity::class.java)
        saveData(moneda)
        startActivity(intent)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun saveData(moneda: String) {
        val tipsSwitch : Switch = findViewById(R.id.tips1)
        val tips: String = if (tipsSwitch.isChecked) {
            "yes"
        } else {
            "no"
        }
        val nombreLocal : String = findViewById<EditText>(R.id.NLocal).text.toString()
        val nombreServidor : String = findViewById<EditText>(R.id.URLServicio).text.toString()
        val nombreIdTienda : String = findViewById<EditText>(R.id.IDTienda).text.toString()
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply{
            putString("STATUSTIPS", tips)
        }.apply()
        editor.apply{
            putString("LOCALNOMBRE", nombreLocal)
        }.apply()
        editor.apply{
            putString("LOCALMONEDA", moneda)
        }.apply()
        editor.apply{
            putString("LOCALSERVER", nombreServidor)
        }.apply()
        editor.apply{
            putString("LOCALID", nombreIdTienda)
        }.apply()

        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }


}