package com.example.network.models

object Urls {
    private const val BASE_URL = "http://192.168.1.7:7081"

    const val LOGIN = "$BASE_URL/User/sign_in"
    const val REGISTER = "$BASE_URL/User/create"
    const val COMPANY = "$BASE_URL/Company"
    const val CLIENT = "$BASE_URL/Client"
    const val BRANCH = "$BASE_URL/Branch"

    fun getCompany(companyId: String) = "$COMPANY/$companyId"
    fun getClient(clientId: String) = "$CLIENT/$clientId"
    fun getBranch(branchId: String) = "$BRANCH/$branchId"


}