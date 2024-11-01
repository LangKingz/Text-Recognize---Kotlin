package com.dicoding.asclepius.data

import retrofit2.http.GET

interface ApiService {
    @GET("top-headlines?q=cancer&category=health&language=en&apiKey=697d84eb77f641239a88dfe81c94f93e")
    suspend fun getNews(): ApiResponse
}

data class ApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

data class Source(
    val id: String?,
    val name: String
)