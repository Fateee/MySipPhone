package com.sip.phone.net;

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface PhoneApiList {

    @POST("yestone/auth")
    fun authPhoneNum(@Body map: Map<String, String>?) : Observable<Response<String>>

    @POST("yestone/log")
    fun recordCallLog(@Body map: Map<String, String>?) : Observable<Response<String>>

    @POST("yestone/login")
    fun loginAndCheck(@Body map: Map<String, String>?) : Observable<Response<String>>
}
