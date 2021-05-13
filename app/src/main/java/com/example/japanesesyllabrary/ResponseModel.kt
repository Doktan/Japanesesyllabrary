package com.example.japanesesyllabrary

data class ResponseModel(
    var message: String? = null,
    var data: Array<String>? = null
)

data class ModesResponse(
    var message: String? = null,
    var modes: Array<String>? = null,
    var discriptions: Array<String>? = null
    )