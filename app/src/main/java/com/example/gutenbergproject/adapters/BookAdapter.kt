package com.example.gutenbergproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gutenbergproject.models.Book
import com.example.gutenbergproject.databinding.ItemBookBinding

class BookAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // ViewHolder class for holding and binding the UI
    inner class BookViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            with(binding) {
                tvTitle.text = book.title.ifEmpty { "No Title Available" }

                // Join all authors' names or use fallback
                tvAuthor.text = if (book.authors.isNotEmpty()) {
                    book.authors.joinToString(", ") { it.name }
                } else {
                    "Unknown Author"
                }

                Glide.with(imgBook.context)
                    .load(book.formats.coverImage)
                    .into(imgBook)
                // You can add more bindings here like cover image, etc.
                // For example: Glide.with(imageView.context).load(book.coverUrl).into(imageView)

                // Handle click
                root.setOnClickListener {
                    onBookClick(book)
                }
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size
}
