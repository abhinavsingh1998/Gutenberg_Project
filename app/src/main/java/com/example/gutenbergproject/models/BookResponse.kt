package com.example.gutenbergproject.models

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("results")
    val results: List<Book>
)

data class Book(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("authors")
    val authors: List<Author>,

    @SerializedName("translators")
    val translators: List<Translator>,

    @SerializedName("subjects")
    val subjects: List<String>,

    @SerializedName("bookshelves")
    val bookshelves: List<String>,

    @SerializedName("languages")
    val languages: List<String>,

    @SerializedName("copyright")
    val copyright: Boolean,

    @SerializedName("media_type")
    val mediaType: String,

    @SerializedName("formats")
    val formats: Formats,

    @SerializedName("download_count")
    val downloadCount: Int
)

data class Author(
    @SerializedName("name")
    val name: String,

    @SerializedName("birth_year")
    val birthYear: Int?,

    @SerializedName("death_year")
    val deathYear: Int?
)

typealias Translator = Author

data class Formats(
    @SerializedName("text/html")
    val textHtml: String? = null,

    @SerializedName("text/html; charset=iso-8859-1")
    val textHtmlIso: String? = null,

    @SerializedName("application/epub+zip")
    val epub: String? = null,

    @SerializedName("application/x-mobipocket-ebook")
    val mobi: String? = null,

    @SerializedName("text/plain")
    val plainTextUtf8: String? = null,

    @SerializedName("text/plain; charset=iso-8859-1")
    val plainTextIso: String? = null,

    @SerializedName("text/plain; charset=us-ascii")
    val plainTextAscii: String? = null,

    @SerializedName("application/rdf+xml")
    val rdf: String? = null,

    @SerializedName("image/jpeg")
    val coverImage: String? = null
)


