package com.bsaldevs.storeparser.parsers

import android.os.AsyncTask
import android.util.Log
import com.bsaldevs.storeparser.Product
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class Parser : ProductParsable {

    companion object {
        const val PARAM_COUNT = 3

        class DownloadHTML(private val url : String, private val onHTMLDownload: OnHTMLDownload) : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                val document = Jsoup.connect(url).get()
                onHTMLDownload.onLoaded(document)
                return null
            }
        }
    }

    fun parseProduct(url: String, onParseProductListener: OnParseProductListener) {
        Log.d("CDA", "parseProduct start")

        val downloadHTML = DownloadHTML(url, object : OnHTMLDownload {
            override fun onLoaded(document: Document) {

                var mPrice = 0
                var mImageURL = ""
                var mName = ""

                var parsedParamsCount = 0

                ParsePriceTask(this@Parser, document, object : OnParseProductParamListener {
                    override fun onComplete(value: String) {
                        mPrice = value.toInt()
                        Log.d("CDA", "parseProduct: price parsed - $mPrice")
                        parsedParamsCount++
                        if (parsedParamsCount == Parser.PARAM_COUNT)
                            onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
                    }
                }).execute()

                ParseNameTask(this@Parser, document, object : OnParseProductParamListener {
                    override fun onComplete(value: String) {
                        mName = value
                        Log.d("CDA", "parseProduct: name parsed - $value")
                        parsedParamsCount++
                        if (parsedParamsCount == Parser.PARAM_COUNT)
                            onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
                    }
                }).execute()

                ParseImageUrlTask(this@Parser, document, object : OnParseProductParamListener {
                    override fun onComplete(value: String) {
                        mImageURL = value
                        Log.d("CDA", "parseProduct: imageURL parsed - $value")
                        parsedParamsCount++
                        if (parsedParamsCount == Parser.PARAM_COUNT)
                            onParseProductListener.onComplete(Product(url, mPrice, mImageURL, mName))
                    }

                }).execute()
            }
        })
        downloadHTML.execute()
    }

    private class ParsePriceTask(val parser: Parser, val document: Document, val onParseProductParamListener: OnParseProductParamListener) :
        AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String {
            return parser.parseProductPrice(document)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            onParseProductParamListener.onComplete(result)
        }
    }

    private class ParseImageUrlTask(
        val parser: Parser,
        val document: Document,
        val onParseProductParamListener: OnParseProductParamListener
    ) :
        AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String {
            return parser.parseProductImageURL(document)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            onParseProductParamListener.onComplete(result)
        }
    }

    private class ParseNameTask(val parser: Parser, val document: Document, val onParseProductParamListener: OnParseProductParamListener) :
        AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String {
            return parser.parseProductName(document)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            onParseProductParamListener.onComplete(result)
        }
    }

    interface OnParseProductListener {
        fun onComplete(product: Product)
    }

    interface OnParseProductParamListener {
        fun onComplete(value: String)
    }

    interface OnHTMLDownload {
        fun onLoaded(document: Document)
    }

}