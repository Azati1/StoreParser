package com.bsaldevs.storeparser

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main_content.*
import kotlinx.android.synthetic.main.fragment_main_content.view.*
import kotlinx.android.synthetic.main.recycler_observable_item.view.*
import java.lang.StringBuilder
import java.util.ArrayList

class MainContentFragment : Fragment(), MyApplication.OnProductAction {

    override fun onProductCountChanged() {
        recyclerAdapter.notifyDataSetChanged()
        products = application.getProducts()
        Log.d("CDA_MCF", "produst size is - " + products.size)
        if (products.size > 0) {
            mainHint.setVisibility(View.GONE)
        }
    }

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var recyclerAdapter : Adapter
    private lateinit var products : List<Product>
    private lateinit var application: MyApplication
    private lateinit var mainHint : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CDA", "onCreate: in main content fragment")
        application = context!!.applicationContext as MyApplication
        application.registerOnProductActionListener(this)
        products = application.getProducts()
        Log.d("CDA_MCF", "products size - " + products.size)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_content, container, false)

        mainHint = view.main_hint

        if (products.size > 0)
            view.main_hint.setVisibility(View.GONE)

        setupRecycledView(view.recycler_observable_goods)
        return view
    }

    private fun setupRecycledView(recyclerView : RecyclerView) {
        //val resId = R.anim.layout_animation_fall_down
        //val animation = android.view.animation.AnimationUtils.loadLayoutAnimation(this@MainActivity, resId)

        //recycler_observable_goods.setLayoutAnimation(animation)
        val horizontalLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
        recyclerAdapter = Adapter()
        recyclerAdapter.notifyDataSetChanged()
        recyclerView.adapter = recyclerAdapter
    }

    private inner class Adapter : RecyclerView.Adapter<Adapter.ItemViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Adapter.ItemViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_observable_item, viewGroup, false)
            return Adapter().ItemViewHolder(v)
        }

        override fun onBindViewHolder(holder: Adapter.ItemViewHolder, i: Int) {
            val recyclerItem = RecyclerItem(products[i], "product_name")
            holder.bind(recyclerItem)
        }

        override fun getItemCount(): Int {
            return products.size
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(recyclerItem: RecyclerItem) {
                itemView.product_name.text = recyclerItem.product.name
                itemView.price_value_with_currency.text = priceWithCurrency(recyclerItem.product.lastPrice)
                DownloadImageTask(itemView.product_image).execute(recyclerItem.product.imageURL)
            }

            fun priceWithCurrency(price: Int) : String {
                val currency = "руб."
                val builder = StringBuilder()
                builder.append(price).append(" ").append(currency)
                return builder.toString()
            }

        }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainContentFragment().apply {
            }
    }

    private class RecyclerItem(val product : Product, val name : String)

    private class DownloadImageTask(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlDisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(urlDisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }

            return mIcon11
        }

        override fun onPostExecute(result: Bitmap) {
            bmImage.setImageBitmap(result)
        }
    }
}
