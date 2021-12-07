package com.graphic.key.task

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSender(private val url: String) {

    suspend fun send(vararg data: Any): String {

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

        return withContext(Dispatchers.IO) {
            try {
                val response = client.post<HttpResponse>(url) {
                    contentType(ContentType.Application.Json)
                    body = data[0]
                }
                Log.d("send data", response.toString())
            } catch (e: Exception) {
                e.localizedMessage
            }

            "Данные успешно отправлены"
        }

    }

    companion object {
        private const val USER_NAME = "etpmv"
        private const val PASSWORD = "1qaz@WSX"
    }
}
