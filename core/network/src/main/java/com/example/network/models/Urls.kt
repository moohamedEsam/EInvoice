package com.example.network.models

object Urls {
    private const val BASE_URL = "http://192.168.1.5:7081"
    const val REFRESH_TOKEN = "$BASE_URL/User/refresh_token"
    const val LOGIN = "$BASE_URL/User/sign_in"
    const val REGISTER = "$BASE_URL/User/create"
    const val COMPANY = "$BASE_URL/Company"
    const val CLIENT = "$BASE_URL/Client"
    const val BRANCH = "$BASE_URL/Branch"
    const val ITEM = "$BASE_URL/Item"
    private const val CONSTANTS = "$BASE_URL/Constants"
    const val UNIT_TYPES = "$CONSTANTS/unit_types"
    fun getCompany(companyId: String) = "$COMPANY/$companyId"
    fun getClient(clientId: String) = "$CLIENT/$clientId"
    fun getBranch(branchId: String) = "$BRANCH/$branchId"

    fun getItem(itemId: String) = "$ITEM/$itemId"


}