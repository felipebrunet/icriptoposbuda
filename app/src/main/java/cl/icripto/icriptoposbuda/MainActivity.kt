package cl.icripto.icriptoposbuda

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_IcriptoPOSBuda)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)



        val input: TextView = findViewById(R.id.input)
        val buttonBotondepago: Button = findViewById(R.id.link_twitter)
        val button1: Button = findViewById(R.id.button_1)
        val button2: Button = findViewById(R.id.button_2)
        val button3: Button = findViewById(R.id.button_3)
        val button4: Button = findViewById(R.id.button_4)
        val button5: Button = findViewById(R.id.button_5)
        val button6: Button = findViewById(R.id.button_6)
        val button7: Button = findViewById(R.id.button_7)
        val button8: Button = findViewById(R.id.button_8)
        val button9: Button = findViewById(R.id.button_9)
        val button0: Button = findViewById(R.id.button_0)
        val buttonDot: Button = findViewById(R.id.button_dot)
        val buttonBorrar: Button = findViewById(R.id.button_borrar)

        val defaultMoneda = "CLP"
        val defaultLocal = "Restaurant A"
        val defaultServer = ""
        val defaultStoreId = ""
        val defaultTips = "no"
        val defaultPin = ""

        //        Loading preexisting settings. If there are none, then load the default (view the "default... constants) values.

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val nombreLocal = sharedPreferences.getString("LOCALNOMBRE", defaultLocal).toString()
        val moneda = sharedPreferences.getString("LOCALMONEDA", defaultMoneda).toString()
        val pin = sharedPreferences.getString("LOCALPIN", defaultPin).toString()
        val server = sharedPreferences.getString("LOCALSERVER", defaultServer).toString()
        sharedPreferences.getString("LOCALID", defaultStoreId).toString()
        val tips = sharedPreferences.getString("STATUSTIPS", defaultTips).toString()


        val adjustScreenButton = findViewById<Button>(R.id.botonAjustes)
        adjustScreenButton.setOnClickListener {
            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))//(this)
            if (pin == "") {
                builder.setTitle("Crear clave Pin")
                val inputPin = EditText(ContextThemeWrapper(this, R.style.AlertInputCustom))
                builder.setView(inputPin)
                builder.setPositiveButton("Ok") {
                        dialog, _ -> dialog.dismiss()
                    val sharedPreferencesPin : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                    val editor : SharedPreferences.Editor = sharedPreferencesPin.edit()
                    editor.apply{
                        putString("LOCALPIN", inputPin.text.toString())
                    }.apply()
                    Toast.makeText(this, "Pin Guardado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ActividadAjustes::class.java)
                    startActivity(intent)

                }
                builder.show()
            } else{
                builder.setTitle("Ingrese Pin secreto")
                val inputPin = EditText(ContextThemeWrapper(this, R.style.AlertInputCustom))
                builder.setView(inputPin)
                builder.setPositiveButton("Ok") {
                        dialog, _ -> dialog.dismiss()

                    if(inputPin.text.toString() == pin) {
                        Toast.makeText(this, "Acceso autorizado", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ActividadAjustes::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show()
                    }

                }
                builder.show()
            }


        }

        findViewById<TextView>(R.id.moneda).text = moneda
        findViewById<TextView>(R.id.tituloLocal).text = nombreLocal

        val initString: String = getString(R.string.cifra_ini)

//        if (server.isEmpty() || localID.isEmpty())
        if (server.isEmpty()) {
            input.text = addToInputText(initString, input)
        } else {

//        Setting the functions of buttons

            buttonBorrar.setOnClickListener {
                input.text = ""
                input.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            button1.setOnClickListener {
                input.text = addToInputText("1", input)
            }
            button2.setOnClickListener {
                input.text = addToInputText("2", input)
            }
            button3.setOnClickListener {
                input.text = addToInputText("3", input)
            }
            button4.setOnClickListener {
                input.text = addToInputText("4", input)
            }
            button5.setOnClickListener {
                input.text = addToInputText("5", input)
            }
            button6.setOnClickListener {
                input.text = addToInputText("6", input)
            }
            button7.setOnClickListener {
                input.text = addToInputText("7", input)
            }
            button8.setOnClickListener {
                input.text = addToInputText("8", input)
            }
            button9.setOnClickListener {
                input.text = addToInputText("9", input)
            }
            button0.setOnClickListener {
                if (input.text.isEmpty()) {
                    // Show Error Message
                    input.text = addToInputText("", input)
                } else {
                    input.text = addToInputText("0", input)
                }
            }
            buttonDot.setOnClickListener {
                if (input.text.isEmpty()) {
                    input.text = addToInputText("0.", input)
                } else {
                    input.text = addToInputText(".", input)
                }
            }


            //        Setting the function of the "Pay" button
            buttonBotondepago.setOnClickListener {
                if (input.text.isNotEmpty()) {
                    try {
                        val price: Double = input.text.toString().toDouble()
                        if (tips == "yes") {
                            payWithTip(price, moneda)
                        } else {
                            if ((price > 149 && moneda == "CLP") ||
                                (price > 50 && moneda == "ARS") ||
                                (price > 0.9 && moneda == "PEN") ||
                                (price > 860 && moneda == "COP")) {
                                goPayment(price)
                            }
                        }

                    } catch (e: Exception) {
                        input.text = "Error"
                        input.setTextColor(ContextCompat.getColor(this, R.color.red))
                    }

                }
            }
        }
    }

    private fun addToInputText(buttonValue: String, input: TextView): String {
        return "${input.text}$buttonValue"
    }

    //    Generic function for generating invoice, with all parameters required
    private fun goPayment(price: Double) {

//        val urlBuda = "https://www.buda.com/api/v2/pay/${server}/invoice?amount=${price}&description=cobro_${nombreLocal}"
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply{
            putString("PRICE", price.toString())
        }.apply()

        val intent = Intent(this, ActividadPago::class.java)
        startActivity(intent)
//        val urlIcripto = "${server}/api/v1/invoices?storeId=${localID}&price=${price}&checkoutDesc=${nombreLocal}&currency=${moneda}"
//        startActivity(Intent.parseUri(urlBuda, 0))
    }

    private fun payWithTip(price: Double, moneda: String) {

        val noTip = R.string.no_tip
        var tipValue: Double
        val tipString = getString(noTip)
        val tipMessage = R.string.tip_message
        val items = arrayOf(tipString, "5%", "10%", "15%", "20%")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(tipMessage)
        builder.setItems(items) { _, which->
            tipValue = when (items[which]) {
                "5%" -> 1.05
                "10%" -> 1.1
                "15%" -> 1.15
                "20%" -> 1.2
                else -> {
                    1.0
                }
            }

            if ((price * tipValue > 149 && moneda == "CLP") ||
                (price * tipValue > 50 && moneda == "ARS") ||
                (price * tipValue > 0.9 && moneda == "PEN") ||
                (price * tipValue > 860 && moneda == "COP")) {
                goPayment(price * tipValue)
            }
        }
        builder.show()
    }


//    fun getPassword() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Ingrese Password")
//        val input = EditText(this)
//        input.inputType = InputType.TYPE_CLASS_NUMBER
//        builder.setView(input)
//        builder.setNeutralButton(
//            "Ok"
//        ) { dialog, which -> dialog.cancel() }
//
//
//
//        builder.show()
//
//    }
}