package com.graphic.key.task

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

class SendDataTask(private val weakReference: WeakReference<Context>, private val url: String) : AsyncTask<Any, Int, String>() {

    override fun doInBackground(vararg data: Any): String {
        val scope = CoroutineScope(SupervisorJob())
        val client = HttpClient(Android) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = USER_NAME, password = PASSWORD)
                    }
                }
            }
            install(JsonFeature)
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
            }
        }

        val request = scope.async {
            client.post<HttpResponse>(url) {
                contentType(ContentType.Application.Json)
                body = data[0]
            }
        }
        try {
            val response = runBlocking {
                request.await()
            }

            Log.d("send data", response.toString())
        } catch (e: Exception) {
            return e.localizedMessage ?: "Не удалось отправить данные"
        }

        return "Данные успешно отправлены"
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        val context = weakReference.get()
        if (context != null) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val USER_NAME = "etpmv"
        private const val PASSWORD = "1qaz@WSX"
    }
}