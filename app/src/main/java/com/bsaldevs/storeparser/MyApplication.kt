package com.bsaldevs.storeparser

import android.app.Application
import android.arch.persistence.room.Room
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bsaldevs.storeparser.parsers.MvideoStoreParser
import java.util.*

class MyApplication : Application() {

    private val products = ArrayList<Product>()
    private val listeners = ArrayList<OnProductAction>()
    private lateinit var database : AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "production")
            .allowMainThreadQueries()
            .build()

        val products = getProducts()
        products.forEach { p ->
            //removeProduct(p)
            this.products.add(p)
        }

    }

    fun addProduct(product: Product) {
        products.add(product)
        database.productDao().insertAllProducts(product)
        listeners.forEach { listener ->
            listener.onProductCountChanged()
        }
    }

    private fun removeProduct(product: Product) {
        Log.d("CDA", "before removing from app - ${products.size}")
        Log.d("CDA", "before removing from db - ${getProducts().size}")
        products.remove(product)
        database.productDao().removeProduct(product)
        Log.d("CDA", "after removing from app - ${products.size}")
        Log.d("CDA", "after removing from db - ${getProducts().size}")
        listeners.forEach { listener ->
            listener.onProductCountChanged()
        }
    }

    fun getProducts() : List<Product> {
        return database.productDao().getAllProducts()
    }

    fun registerOnProductActionListener(onProductAction: OnProductAction) {
        listeners.add(onProductAction)
    }

    private fun unregisterOnProductActionListner(onProductAction: OnProductAction) {
        listeners.remove(onProductAction)
    }

    interface OnProductAction {
        fun onProductCountChanged()
    }

}