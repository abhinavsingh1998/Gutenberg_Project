package com.example.gutenbergproject.repositories

import com.example.gutenbergproject.R
import com.example.gutenbergproject.models.CategoryItem

object CategoryRepository  {
    fun getCategories(): List<CategoryItem> = listOf(
        CategoryItem("FICTION", R.drawable.fiction),
        CategoryItem("DRAMA", R.drawable.drama),
        CategoryItem("HUMOUR", R.drawable.humour),
        CategoryItem("POLITICS", R.drawable.history),
        CategoryItem("PHILOSOPHY", R.drawable.philosophy),
        CategoryItem("HISTORY", R.drawable.history),
        CategoryItem("ADVENTURE", R.drawable.adventure)
    )
}