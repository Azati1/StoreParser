package com.bsaldevs.storeparser

import java.io.Serializable

class Product(val url: String, val lastPrice: Int, val imageURL: String, val name: String) : Serializable