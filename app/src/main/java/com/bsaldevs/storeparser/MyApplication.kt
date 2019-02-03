package com.bsaldevs.storeparser

import android.app.Application
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase

class MyApplication : Application() {

    private val products = ArrayList<Product>()
    private val listeners = ArrayList<OnProductAction>()
    private lateinit var database : AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "production")
            .allowMainThreadQueries()
            .build()
    }

    public fun addProduct(product: Product) {
        products.add(product)
        database.productDao().insertAllProducts(product)
        listeners.forEach { listener ->
            listener.onProductCountChanged()
        }
    }

    public fun removeProduct(product: Product) {
        products.remove(product)
        listeners.forEach { listener ->
            listener.onProductCountChanged()
        }
    }

    public fun getProducts() : List<Product> {
        return database.productDao().getAllProducts()
    }

    public fun registerOnProductActionListener(onProductAction: OnProductAction) {
        listeners.add(onProductAction)
    }

    public fun unregisterOnProductActionListner(onProductAction: OnProductAction) {
        listeners.remove(onProductAction)
    }

    interface OnProductAction {
        fun onProductCountChanged()
    }

}