package com.bsaldevs.storeparser

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun getAllProducts() : List<Product>

    @Insert
    fun insertAllProducts(vararg products : Product)

    @Delete
    fun removeProduct(product: Product)
}