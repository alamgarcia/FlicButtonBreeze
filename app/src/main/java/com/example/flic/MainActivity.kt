package com.example.flic

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.flic.lib.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import android.os.AsyncTask.execute
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            Toast.makeText(context, "Boton Arriba", Toast.LENGTH_SHORT).show()

        } else {
            Log.d("Boton","Boton Presionado")
            Toast.makeText(context, "Boton Presionado", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onButtonRemoved(context: Context, button: FlicButton) {
        Log.d("Boton","Boton desconectado")


    }
}
