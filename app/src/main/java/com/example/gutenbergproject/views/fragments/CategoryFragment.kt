package com.example.gutenbergproject.views.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gutenbergproject.databinding.FragmentCategoryBinding
import com.example.gutenbergproject.utility.ApiResult
import com.example.gutenbergproject.utility.LoadType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.gutenbergproject.adapters.BookAdapter
import com.example.gutenbergproject.viewModels.BookViewModel
import com.example.gutenbergproject.models.Book


@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookViewModel by viewModels()
    private val args: CategoryFragmentArgs by navArgs()

    private val bookList = mutableListOf<Book>()
    private lateinit var bookAdapter: BookAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = args.categoryName
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()

        }

        initRecyclerView()
        observeBookState()
        setupSearchListener()

        // Fetch initial books for the category
        fetchBooksWithPrefix("")
    }

    /**
     * Sets up RecyclerView with adapter and pagination scroll listener
     */
    private fun initRecyclerView() = binding.rvBooks.run {
        bookAdapter = BookAdapter(bookList) { selectedBook ->
            handleBookClick(selectedBook)
        }
        layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = bookAdapter
        setHasFixedSize(true)
        addOnScrollListener(paginationListener())
    }

    /**
     * Observes ViewModel state and reacts to loading, success, or error
     */
    private fun observeBookState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> showLoading()
                        is ApiResult.Success -> {
                            hideLoading()
                            handleSuccess(result.data.results, result.data.count)
                        }
                        is ApiResult.Error -> {
                            hideLoading()
                            showError(result.message)
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets up search bar with debounce and triggers API call
     */
    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300L) // Debounce delay

                val query = editable?.toString()?.trim().orEmpty()
                bookList.clear()
                bookAdapter.notifyDataSetChanged()
                fetchBooksWithPrefix(query)
            }
        }
    }

    /**
     * Calls ViewModel to fetch books with the final query
     */
    private fun fetchBooksWithPrefix(searchText: String, loadType: LoadType = LoadType.INITIAL) {
        val finalQuery = if (searchText.isNotEmpty()) {
            "${args.categoryName} $searchText"
        } else {
            args.categoryName
        }

        viewModel.fetchBooks(finalQuery, loadType)

    }

    /**
     * Appends newly fetched books to the list and updates adapter
     */
    private fun handleSuccess(books: List<Book>, totalCount: Int) {
        if (books.isEmpty()) {
            Toast.makeText(requireContext(), "No books found", Toast.LENGTH_SHORT).show()
            return
        }

        val newBooks = books.filterNot { incoming -> bookList.any { existing -> existing.id == incoming.id } }
        val start = bookList.size
        bookList.addAll(newBooks)
        bookAdapter.notifyItemRangeInserted(start, newBooks.size)

    }

    /**
     * Handles pagination when user scrolls to the bottom
     */
    private fun paginationListener() = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(rv, dx, dy)
            val layout = rv.layoutManager as? GridLayoutManager ?: return

            val total = layout.itemCount
            val visible = layout.childCount
            val firstVisible = layout.findFirstVisibleItemPosition()

            val thresholdReached = firstVisible + visible + 2 >= total

            if (!viewModel.isLoading && viewModel.hasMore && thresholdReached) {
                val typedQuery = binding.etSearch.text?.toString()?.trim().orEmpty()
                fetchBooksWithPrefix(typedQuery, LoadType.NEXT_PAGE)
            }
        }

    }

    /**
     * Handles book click by checking for viewable formats and opening in browser
     */
    private fun handleBookClick(book: Book) {
        val formats = book.formats

        // List of preferred MIME types in order
        val preferredFormats = listOf("text/html", "application/pdf", "text/plain")

        val matchedUrl = preferredFormats
            .firstNotNullOfOrNull { formats }

        if (matchedUrl != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formats.textHtml))
            startActivity(intent)
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Format Unavailable")
                .setMessage("No viewable version available.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(message: String) {
        Log.e("CategoryFragment", message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchJob?.cancel()
    }
}

