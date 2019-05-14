package com.example.flic

import android.content.Context
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
import java.io.IOException


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Boton","Configurando Credenciales")

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

        } else {
            Log.d("Boton","Boton Abajo")
            execute {
                try {
                    val client = OkHttpClient()

                    val mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    val body = RequestBody.create(
                        mediaType,
                        "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"family\"\r\n\r\nAAADEVRFID\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"type\"\r\n\r\nAAADEVRFIDLOCALIZATION\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"version\"\r\n\r\n1.0\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"eventBody\"\r\n\r\n{'correoElectronico':'garcia76@avaya.com','Param1':'Param1','Param2':'Param2','Param2':'Param2','Param3':'Param3','Param4':'Param4','Param5':'Param5'}\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--"
                    )
                    val request = Request.Builder()
                        .url("http://breeze2-132.collaboratory.avaya.com/services/EventingConnector/events")
                        .post(body)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Host", "breeze2-132.collaboratory.avaya.com")
                        .addHeader("accept-encoding", "gzip, deflate")
                        .addHeader("content-length", "663")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("cache-control", "no-cache")
                        .build()

                    val response = client.newCall(request).execute()


                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            Toast.makeText(context, "Boton Presionado", Toast.LENGTH_SHORT).show()



        }
    }


    override fun onButtonRemoved(context: Context, button: FlicButton) {
        Log.d("Boton","Boton desconectado")


    }
}
