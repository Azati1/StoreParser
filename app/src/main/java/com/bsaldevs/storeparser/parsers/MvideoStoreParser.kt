package com.bsaldevs.storeparser.parsers

import android.util.Log
import org.jsoup.nodes.Document

class MvideoStoreParser : Parser() {

    override fun parseProductPrice(document: Document): String {
        val elements = document.getElementsByTag("meta")
        var price = "null"
        for (element in elements) {
            if (element.select("[itemprop=price]").toString() != "") {
                price = element.select("[itemprop=price]").attr("content")
                Log.d("CDA_PARSER", "" + price)
                break
            }
        }
        return price
    }

    override fun parseProductName(document: Document): String {
        val elements = document.select("span")
        var name = ""
        for (element in elements) {
            if (element.select("[itemprop=name]").attr("content") != "") {
                name = element.select("[itemprop=name]").attr("content")
                Log.d("CDA_PARSER", name)
                break
            }
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

    override fun getStore(): Store {
        return Parser.Store.M_VIDEO
    }

}