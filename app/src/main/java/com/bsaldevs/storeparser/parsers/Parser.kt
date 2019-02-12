package com.bsaldevs.storeparser.parsers

import com.bsaldevs.storeparser.Product

abstract class Parser() {

    enum class Store {
        DNS_SHOP,
        ELDORADO,
        M_VIDEO
    }

    protected abstract fun getStore() : Store
    protected abstract fun parseProduct(url: String, onParseProductListener: OnParseProductListener)
    protected abstract fun parseName(url: String, onParseProductNameListener: OnParseProductNameListener)
    protected abstract fun parseImageURL(url: String, onParseProductImageURLListener: OnParseProductImageURLListener)
    protected abstract fun parsePrice(url: String, onParseProductPriceListener: OnParseProductPriceListener)

    interface OnParseProductListener {
        fun onComplete(product: Product)
    }

    interface OnParseProductPriceListener {
        fun onPriceParsed(price: Int)
    }

    interface OnParseProductImageURLListener {
        fun onImageURLParsed(imageURL: String)
    }

    interface OnParseProductNameListener {
        fun onNameParsed(name: String)
    }
}