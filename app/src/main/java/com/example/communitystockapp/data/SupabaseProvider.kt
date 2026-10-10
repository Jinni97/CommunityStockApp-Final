package com.example.communitystockapp.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress

object SupabaseProvider {
    // URL ที่แก้ไข Project ID ถูกต้องแล้ว
    private const val SUPABASE_URL = "https://ufkgmibmbmsvlnqevuzo.supabase.co"

    // คีย์ Legacy Anon Key (JWT) ตัวจริงที่ก๊อปปี้มา
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVma2dtaWJtYm1zdmxucWV2dXpvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE1OTM1MDksImV4cCI6MjEwNzE2OTUwOX0.RpYVzFbFp5yktxKs_K8xjsi4w-D96ra42aWrWHCB4lI"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        httpEngine = OkHttp.create {
            config {
                val bootstrapClient = OkHttpClient.Builder().build()
                val dnsOverHttps = DnsOverHttps.Builder()
                    .client(bootstrapClient)
                    .url("https://dns.google/dns-query".toHttpUrl())
                    .bootstrapDnsHosts(
                        InetAddress.getByName("8.8.8.8"),
                        InetAddress.getByName("8.8.4.4")
                    )
                    .build()

                dns(dnsOverHttps)
            }
        }
        install(Postgrest)
    }
}