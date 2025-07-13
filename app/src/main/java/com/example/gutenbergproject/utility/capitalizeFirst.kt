package com.example.gutenbergproject.utility

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}
