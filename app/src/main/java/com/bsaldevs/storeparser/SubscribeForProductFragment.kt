package com.bsaldevs.storeparser

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bsaldevs.storeparser.parsers.MvideoStoreParser
import com.bsaldevs.storeparser.parsers.Parser
import kotlinx.android.synthetic.main.fragment_subscribe_for_goods.*

class SubscribeForProductFragment : Fragment(), OnActionButtonListener {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var application: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = context!!.applicationContext as MyApplication
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscribe_for_goods, container, false)
    }

    private fun subscribeForGoods(productURL : String) {
        val parser = Parser.create(productURL)
        parser.parseProduct(productURL,object : Parser.OnParseProductListener {
            override fun onComplete(product: Product) {
                application.addProduct(product)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onSubscribeForProductFragmentInteraction(status : String)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SubscribeForProductFragment().apply {
            }
    }

    override fun onFloatingActionButtonClick() {

        val stringURL = edit_url.text.toString()

        if (!checkURLCorrect(stringURL)) {
            (activity as MainActivity).onSubscribeForProductFragmentInteraction("incorrect url")
            return
        }

        subscribeForGoods(stringURL)
        clearFields()

        (activity as MainActivity).onSubscribeForProductFragmentInteraction("ok")
    }

    private fun checkURLCorrect(url : String): Boolean {
        return url != ""
    }

    private fun clearFields() {
        edit_url.setText("")
    }
}
