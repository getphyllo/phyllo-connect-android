package com.connect.demo

data class TokenRequest(val user_id: String,
                        val products: ArrayList<String> = arrayListOf("IDENTITY", "ENGAGEMENT"))