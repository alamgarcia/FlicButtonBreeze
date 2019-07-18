package com.example.flic

import android.content.Context
import android.content.Intent
import android.os.AsyncTask.execute
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.flic.lib.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var myPreferences = "myPrefs"
        var sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        var endpoint = sharedPreferences.getString("endpoint", "http://breeze2-132.collaboratory.avaya.com/services/EventingConnector/events")
        var family = sharedPreferences.getString("family", "AAADEVComiteEmergencia")
        var type = sharedPreferences.getString("type", "AAADEVComiteEmergenciaType")
        var version = sharedPreferences.getString("version", "1.0")
        var email = sharedPreferences.getString("nombreAdmin", "Alejandro")
        var param1 = sharedPreferences.getString("numerosParticipantes", "2310,2311,2312,2314,2314,2316")
        var param2 = sharedPreferences.getString("bridgePhone", "19728106902")
        var param3 = sharedPreferences.getString("bridgeId", "903525552787969#")
        endpoint_txt.setText(endpoint)
        family_txt.setText(family)
        type_txt.setText(type)
        version_txt.setText(version)
        email_txt.setText(email)
        param1_txt.setText(param1)
        param2_txt.setText(param2)
        param3_txt.setText(param3)


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
            editor.putString("endpoint", endpoint_txt.text.toString())
            editor.putString("family", family_txt.text.toString())
            editor.putString("type", type_txt.text.toString())
            editor.putString("version", version_txt.text.toString())
            editor.putString("nombreAdmin", email_txt.text.toString())
            editor.putString("numerosParticipantes", param1_txt.text.toString())
            editor.putString("bridgePhone", param2_txt.text.toString())
            editor.putString("bridgeId", param3_txt.text.toString())
            editor.apply()
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("¿Deses probar tus ajustes ?")
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Aceptar") { dialog, id ->
                    var myPreferences = "myPrefs"
                    var sharedPreferences =  getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                    var endpoint = sharedPreferences.getString("endpoint", "http://breeze2-132.collaboratory.avaya.com/services/EventingConnector/events")
                    var family = sharedPreferences.getString("family", "AAADEVComiteEmergencia")
                    var type = sharedPreferences.getString("type", "AAADEVComiteEmergenciaType")
                    var version = sharedPreferences.getString("version", "1.0")
                    var email = sharedPreferences.getString("nombreAdmin", "Alejandro")
                    var param1 = sharedPreferences.getString("numerosParticipantes", "2310,2311,2312,2314,2314,2316")
                    var param2 = sharedPreferences.getString("bridgePhone", "19728106902")
                    var param3 = sharedPreferences.getString("bridgeId", "903525552787969#")
                    execute {
                        try {
                            val client = OkHttpClient()
                            val mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                            val body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"family\"\r\n\r\n$family\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"type\"\r\n\r\n$type\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"version\"\r\n\r\n$version\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"eventBody\"\r\n\r\n{'nombreAdmin':'$email','numerosParticipantes':'$param1','bridgePhone':'$param2','bridgeId':'$param3'} \r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
                            val request = Request.Builder()
                                .url(endpoint)
                                .post(body)
                                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                                .addHeader("Content-Type", "multipart/form-data")
                                .addHeader("cache-control", "no-cache")
                                .build()
                            val response = client.newCall(request).execute()

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
                .setNegativeButton("Cancelar") { dialog, id -> dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Probar POST")
            alert.show()
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
            Log.d("Boton","Boton Presionado")
            var myPreferences = "myPrefs"
            var sharedPreferences =  context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
            var endpoint = sharedPreferences.getString("endpoint", "http://breeze2-132.collaboratory.avaya.com/services/EventingConnector/events")
            var family = sharedPreferences.getString("family", "AAADEVComiteEmergencia")
            var type = sharedPreferences.getString("type", "AAADEVComiteEmergenciaType")
            var version = sharedPreferences.getString("version", "1.0")
            var email = sharedPreferences.getString("nombreAdmin", "Alejandro")
            var param1 = sharedPreferences.getString("numerosParticipantes", "2310,2311,2312,2314,2314,2316")
            var param2 = sharedPreferences.getString("bridgePhone", "19728106902")
            var param3 = sharedPreferences.getString("bridgeId", "903525552787969#")
            Toast.makeText(context, "Boton Presionado", Toast.LENGTH_SHORT).show()
            execute {
                try {
                    val client = OkHttpClient()
                    val mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    val body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"family\"\r\n\r\n$family\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"type\"\r\n\r\n$type\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"version\"\r\n\r\n$version\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"eventBody\"\r\n\r\n{'nombreAdmin':'$email','numerosParticipantes':'$param1','bridgePhone':'$param2','bridgeId':'$param3'} \r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
                    val request = Request.Builder()
                        .url(endpoint)
                        .post(body)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("Content-Type", "multipart/form-data")
                        .addHeader("cache-control", "no-cache")
                        .build()
                    val response = client.newCall(request).execute()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }
    }


    override fun onButtonRemoved(context: Context, button: FlicButton) {
        Log.d("Boton","Boton desconectado")


    }
}
