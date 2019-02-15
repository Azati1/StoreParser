package com.bsaldevs.storeparser

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.bsaldevs.storeparser.parsers.ProductParsable
import java.io.Serializable

@Entity
class Product(
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "lastPrice") val lastPrice: Int,
    @ColumnInfo(name = "imageURL") val imageURL: String,
    @ColumnInfo(name = "name") val name: String
): Serializable {
    @PrimaryKey(autoGenerate = true)
    private var id : Int = 0

    fun getId() : Int {
        return id
    }

    fun setId(id : Int) {
        this.id = id
    }
}