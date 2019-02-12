package com.bsaldevs.storeparser.parsers

import android.os.AsyncTask
import android.util.Log
import com.bsaldevs.storeparser.Product
import org.jsoup.Jsoup

class DNSShopParser : Parser() {

    override fun parseProduct(url: String, onParseProductListener: Parser.OnParseProductListener) {
        var parseParameter = 0

        var mPrice = 0
        var mImageURL = ""
        var mName = ""

        parsePrice(url, onParseProductPriceListener = object : OnParseProductPriceListener {
            override fun onPriceParsed(price: Int) {
                mPrice = price
                parseParameter++
                if (parseParameter == 3)
                    onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
            }
        })

        parseImageURL(url, onParseProductImageURLListener = object : OnParseProductImageURLListener {
            override fun onImageURLParsed(imageURL: String) {
                mImageURL = imageURL
                parseParameter++
                if (parseParameter == 3)
                    onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
            }
        })

        parseName(url, onParseProductNameListener = object : OnParseProductNameListener {
            override fun onNameParsed(name: String) {
                mName = name
                parseParameter++
                if (parseParameter == 3)
                    onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
            }

        })
    }

    override fun parseName(url: String, onParseProductNameListener: Parser.OnParseProductNameListener) {
        val parseNameTask = ParseNameTask(url, onParseProductNameListener = object : OnParseProductNameListener {
            override fun onNameParsed(name: String) {
                onParseProductNameListener.onNameParsed(name)
            }
        })
        parseNameTask.execute()
    }

    override fun parseImageURL(url: String, onParseProductImageURLListener: Parser.OnParseProductImageURLListener) {
        val parseImageUrlTask =
            ParseImageUrlTask(url, onParseProductImageURLListener = object : OnParseProductImageURLListener {
                override fun onImageURLParsed(imageURL: String) {
                    onParseProductImageURLListener.onImageURLParsed(imageURL)
                }
            })
        parseImageUrlTask.execute()
    }

    override fun parsePrice(url: String, onParseProductPriceListener: Parser.OnParseProductPriceListener) {
        val parsePriceTask = ParsePriceTask(url, onParseProductPriceListener = object : OnParseProductPriceListener {
            override fun onPriceParsed(price: Int) {
                onParseProductPriceListener.onPriceParsed(price)
            }
        })

        parsePriceTask.execute()
    }

    override fun getStore(): Store {
        return Parser.Store.DNS_SHOP
    }

    private class ParsePriceTask(val url: String, val onParseProductPriceListener: OnParseProductPriceListener) :
        AsyncTask<Void, Void, Int?>() {

        override fun doInBackground(vararg p0: Void?): Int? {
            val document = Jsoup.connect(url).get()
            val elements = document.getElementsByTag("meta")

            var price = 0

            for (element in elements) {
                if (element.select("[itemprop=price]").toString() != "") {
                    price = Integer.parseInt(element.select("[itemprop=price]").attr("content"))
                    Log.d("CDA_PARSER", "" + price)
                    break
                }
            }

            return price
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            onParseProductPriceListener.onPriceParsed(result!!)
        }

    }

    private class ParseImageUrlTask(
        val url: String,
        val onParseProductImageURLListener: OnParseProductImageURLListener
    ) :
        AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg p0: Void?): String? {
            val document = Jsoup.connect(url).get()
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

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            onParseProductImageURLListener.onImageURLParsed(result!!)
        }
    }

    private class ParseNameTask(val url: String, val onParseProductNameListener: OnParseProductNameListener) :
        AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg p0: Void?): String? {
            val document = Jsoup.connect(url).get()

            var name = ""

            val elementsDNS = document.select("h1")
            for (element in elementsDNS) {
                if (element.select("[itemprop=name]").text() != "") {
                    name = element.text()
                    break
                    /*name = element.select("[itemprop=name]").attr("content")
                    Log.d("CDA_PARSER", name)
                    break*/
                }
            }

            if(name == ""){
                name = "not found"
            }

            return name
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            onParseProductNameListener.onNameParsed(result!!)
        }
    }

}