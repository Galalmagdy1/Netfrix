package com.example.netfrix.network

import com.example.netfrix.models.MovieResponse
import retrofit2.http.GET

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getMovies(): MovieResponse
}
