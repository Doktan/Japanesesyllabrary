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

data class GameResponse(
    var message: String? = null,
    var prefix: String? = null,
    var syll: Array<String>? = null
)

data class RecordsResponse(
    var message: String? = null,
    var names: Array<String>? = null,
    var records: Array<String>? = null
)
