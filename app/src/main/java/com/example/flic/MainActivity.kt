package com.example.flic

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.flic.lib.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var myPreferences = "myPrefs"
        var sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        var numero = sharedPreferences.getString("numeroemergencia", "123456")
        editText.setText(numero)
        Config().setFlicCredentials()
        try {
            Log.d("Boton","Probar Credenciales")

            FlicManager.getInstance(this)
            { manager-> manager.initiateGrabButton(this@MainActivity)

                Log.d("Boton","Iniciando Flic Grabber")


            }



        } catch (err: FlicAppNotInstalledException) {
            Log.d("Boton","Aplicacion Flic no instalada")

            Toast.makeText(this, "Flic App no esta instalado", Toast.LENGTH_LONG).show()
        }

        fab.setOnClickListener {
            val myPreferences = "myPrefs"
            val sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("numeroemergencia", editText.text.toString())
            editor.apply()
            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Aviso")

            builder.setMessage("Se ha establecido el numero: ${editText.text} para Flic")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES"){dialog, which ->
            }
            val dialog: AlertDialog = builder.create()

            dialog.show()
        }
    }
    fun grabButton(v: View) {
        try {
            FlicManager.getInstance(
                this
            ) { manager -> manager.initiateGrabButton(this@MainActivity) }
        } catch (err: FlicAppNotInstalledException) {
            Toast.makeText(this, "Flic App no esta instalado", Toast.LENGTH_LONG).show()
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        FlicManager.getInstance(this) { manager ->
            var button = manager.completeGrabButton(requestCode, resultCode, data)
            Log.d("Boton","Se ha registrado el boton")

            if (button != null) {
                Log.d("Boton","Existe un boton ")

                button.registerListenForBroadcast(FlicBroadcastReceiverFlags.UP_OR_DOWN or FlicBroadcastReceiverFlags.REMOVED)
                Log.d("Boton","Broadcast registrado")

                Toast.makeText(this@MainActivity, "Boton Conectado", Toast.LENGTH_LONG).show()
                Log.d("Boton","Se ha conectado un boton")

            } else {
                Toast.makeText(this@MainActivity, "Sin Boton", Toast.LENGTH_LONG).show()
                Log.d("Boton","No se ha detectado boton")

            }
        }
    }
}

class BroadcastReceiver : FlicBroadcastReceiver() {
    override fun onRequestAppCredentials(context: Context) {


        Log.d("Boton","Aplicacion ha pedido credenciales")
        Config().setFlicCredentials()
        Log.d("Boton","Credenciales Listas")
    }

    override fun onButtonUpOrDown(
        context: Context,
        button: FlicButton,
        wasQueued: Boolean,
        timeDiff: Int,
        isUp: Boolean,
        isDown: Boolean
    ) {
        if (isUp) {
            Log.d("Boton", "Boton Arrriba")

        } else {
            var myPreferences = "myPrefs"
            var sharedPreferences = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
            var numero = sharedPreferences.getString("numeroemergencia", "123456")

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$numero")
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(callIntent)
        }
    }


    override fun onButtonRemoved(context: Context, button: FlicButton) {
        Log.d("Boton","Boton desconectado")


    }
}
