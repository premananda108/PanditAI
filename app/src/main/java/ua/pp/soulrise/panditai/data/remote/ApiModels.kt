package ua.pp.soulrise.panditai.data.remote

data class ApiRequest(
    val data: ApiData
)

data class ApiData(
    val message: String,
    val image: String = "", // Необязательный параметр
    val idb: Int
)

data class ApiResponse(
    val reaction: String,
    val price: String?,
    val response: String,
    val balance: String?
)