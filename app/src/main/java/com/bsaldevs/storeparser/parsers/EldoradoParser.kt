package com.bsaldevs.storeparser.parsers

import android.util.Log
import org.jsoup.nodes.Document

class EldoradoParser : Parser() {

    override fun parseProductPrice(document: Document): String {
        val elements = document.getElementsByTag("meta")
        var price = "null"
        for (element in elements) {
            if (element.select("[itemprop=price]").toString() != "") {
                price = element.select("[itemprop=price]").attr("content")
                Log.d("CDA_ELDO", price)
                break
            }
        }
        return price
    }

    override fun parseProductName(document: Document): String {
        var name = "not found"
        val elementsEldorado = document.getElementsByTag("h1")
        for (element in elementsEldorado) {
            name = element.text()
        }
        return name
    }

    override fun parseProductImageURL(document: Document): String {
        val elements = document.getElementsByTag("meta")
        var imageURL = ""
        for (element in elements) {
            if (element.select("[itemprop=image]").toString() != "") {
                imageURL = element.select("[itemprop=image]").attr("content")
                Log.d("CDA_PARSER", imageURL)
                break
            }
        }

        return imageURL
    }

    override fun getStore(): ProductParsable.Store {
        return ProductParsable.Store.ELDORADO
    }

}