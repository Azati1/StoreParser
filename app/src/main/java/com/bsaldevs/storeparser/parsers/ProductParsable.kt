package com.bsaldevs.storeparser.parsers

import com.bsaldevs.storeparser.Product
import org.jsoup.nodes.Document

interface ProductParsable {

    enum class Store{
        DNS_SHOP,
        ELDORADO,
        M_VIDEO
    }

    fun parseProductPrice(document: Document): String
    fun parseProductName(document: Document): String
    fun parseProductImageURL(document: Document): String
    fun getStore(): Store
}