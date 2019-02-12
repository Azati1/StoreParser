package com.bsaldevs.storeparser.parsers

import android.os.AsyncTask
import android.util.Log
import com.bsaldevs.storeparser.Product
import org.jsoup.Jsoup

class MvideoStoreParser : Parser() {

    override fun getStore(): Store {
        return Parser.Store.M_VIDEO
    }

    override fun parseProduct(url: String, onParseProductListener: OnParseProductListener) {

        Log.d("CDA", "parseProduct start")

        var parseParameter = 0

        var mPrice = 0
        var mImageURL = ""
        var mName = ""

        parsePrice(url, onParseProductPriceListener = object : OnParseProductPriceListener {
            override fun onPriceParsed(price: Int) {
                Log.d("CDA", "parseProduct: price parsed")
                mPrice = price
                parseParameter++
                if (parseParameter == 3)
                    onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
            }
        })

        parseImageURL(url, onParseProductImageURLListener = object : OnParseProductImageURLListener {
            override fun onImageURLParsed(imageURL: String) {
                Log.d("CDA", "parseProduct: imageURL parsed")
                mImageURL = imageURL
                parseParameter++
                if (parseParameter == 3)
                    onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
            }
        })

        parseName(url, onParseProductNameListener = object : OnParseProductNameListener {
            override fun onNameParsed(name: String) {
                Log.d("CDA", "parseProduct: name parsed")
                mName = name
                parseParameter++
                if (parseParameter == 3)
                    onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
            }

        })

    }

    override fun parsePrice(url: String, onParseProductPriceListener: OnParseProductPriceListener) {

        val parsePriceTask = ParsePriceTask(url, onParseProductPriceListener = object : OnParseProductPriceListener {
            override fun onPriceParsed(price: Int) {
                onParseProductPriceListener.onPriceParsed(price)
            }
        })

        parsePriceTask.execute()
    }

    override fun parseImageURL(url: String, onParseProductImageURLListener: OnParseProductImageURLListener) {
        val parseImageUrlTask =
            ParseImageUrlTask(url, onParseProductImageURLListener = object : OnParseProductImageURLListener {
                override fun onImageURLParsed(imageURL: String) {
                    onParseProductImageURLListener.onImageURLParsed(imageURL)
                }
            })
        parseImageUrlTask.execute()
    }

    override fun parseName(url: String, onParseProductNameListener: OnParseProductNameListener) {
        val parseNameTask = ParseNameTask(url, onParseProductNameListener = object : OnParseProductNameListener {
            override fun onNameParsed(name: String) {
                onParseProductNameListener.onNameParsed(name)
            }
        })
        parseNameTask.execute()
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

            for (element in elements) {
                if (element.select("[itemprop=image]").toString() != "") {
                    imageURL = element.select("[itemprop=image]").attr("content")
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

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            onParseProductNameListener.onNameParsed(result!!)
        }
    }

}

