package com.nkechinnaji.thinkbit.news.model.uimodel

import com.nkechinnaji.thinkbit.news.model.Articles

data class ArticleUiModel(
    val id : String,
    val source: String,
    val author: String,
    val title: String,
    val desc: String,
    val url: String,
    val imageUrl: String? = null, // For network images
    val drawableResId: Int? = null, // For local drawable resources
    val publication: String
)

//Map network model to UI model
fun Articles.toUiModel(): ArticleUiModel{
    return ArticleUiModel(
        id = this.source?.id?: "",
        source = this.source?.name ?: "",
        author = this.author?: "",
        title = this.title?: "",
        desc = this.description?: "",
        url = this.url?: "",
        imageUrl = this.urlToImage?: "",
        drawableResId = 0,
        publication = this.publishedAt?: ""
    )
}

//Extension function for list of articles
// network response list to UI model list
fun List<Articles>.toUiModel(): List<ArticleUiModel>{
    return this.map{
        article ->
        article.toUiModel()
    }
}