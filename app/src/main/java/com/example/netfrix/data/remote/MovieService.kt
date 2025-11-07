package com.example.netfrix.data.remote

import com.example.netfrix.data.MovieDetails
import com.example.netfrix.data.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("discover/movie")
    suspend fun getMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetails
}