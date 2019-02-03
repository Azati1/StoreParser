package com.bsaldevs.storeparser

import android.app.Application

class MyApplication : Application() {

    private val products = ArrayList<Product>()
    private val listeners = ArrayList<OnProductAction>()

    public fun addProduct(product: Product) {
        products.add(product)
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

    public fun getProducts() : ArrayList<Product> {
        return products
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