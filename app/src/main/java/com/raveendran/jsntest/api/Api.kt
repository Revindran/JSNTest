package com.raveendran.jsntest.api

import com.raveendran.jsntest.model.PeopleList
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("users")
    suspend fun getAllPeoples(): Response<PeopleList>
}