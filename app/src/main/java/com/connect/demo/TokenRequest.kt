package com.connect.demo

data class TokenRequest(val user_id: String,
                        val redirect_uri: String,
                        val products: ArrayList<String> = arrayListOf("IDENTITY", "ENGAGEMENT"))