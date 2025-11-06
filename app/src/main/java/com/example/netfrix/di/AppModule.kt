package com.example.netfrix.di

import android.content.Context
import androidx.room.Room
import com.example.netfrix.data.MovieDatabase
import com.example.netfrix.network.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "9a68f3bb6f401b5eeb21e015f12552df"

    @Singleton
    @Provides
    fun provideMovieDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        "movie_db"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideMovieDao(movieDatabase: MovieDatabase) = movieDatabase.movieDao()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val original = it.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()
                val request = original.newBuilder().url(url).build()
                it.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieApiService(client: OkHttpClient): MovieApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}
