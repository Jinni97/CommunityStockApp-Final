package com.example.communitystockapp.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun addProduct(product: Product)
    suspend fun updateStock(id: Long, newQty: Int)
    suspend fun deleteProduct(id: Long)
}

class ProductRepositoryImpl : ProductRepository {
    private val client = SupabaseProvider.client

    override suspend fun getProducts(): List<Product> {
        return client.postgrest["products"]
            .select {
                order(column = "id", order = Order.DESCENDING)
            }
            .decodeList<Product>()
    }

    override suspend fun addProduct(product: Product) {
        client.postgrest["products"].insert(product)
    }

    override suspend fun updateStock(id: Long, newQty: Int) {
        client.postgrest["products"].update({
            set("stock_qty", newQty)
        }) {
            filter { eq("id", id) }
        }
    }

    override suspend fun deleteProduct(id: Long) {
        client.postgrest["products"].delete {
            filter { eq("id", id) }
        }
    }
}