package com.bsaldevs.storeparser.parsers

import android.util.Log
import org.jsoup.nodes.Document

class DNSShopParser : Parser() {

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
        var name = "not found"
        val elementsDNS = document.select("h1")
        for (element in elementsDNS) {
            if (element.select("[itemprop=name]").text() != "") {
                name = element.text()
                break
            }
        }
        return name
    }

    override fun parseProductImageURL(document: Document): String {
        val elements = document.getElementsByTag("meta")
        var imageURL = ""
        for(element in elements){
            if(element.select("[property=og:image]").attr("content") != ""){
                imageURL = element.select("[property=og:image]").attr("content")
                Log.d("CDA_PARSER", imageURL)
                break
            }
        }
        return imageURL
    }

    override fun getStore(): Store {
        return Parser.Store.DNS_SHOP
    }

}