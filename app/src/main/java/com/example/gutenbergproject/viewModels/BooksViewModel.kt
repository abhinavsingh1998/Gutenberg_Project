package com.example.gutenbergproject.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutenbergproject.models.BookResponse
import com.example.gutenbergproject.repositories.BookRepository
import com.example.gutenbergproject.utility.ApiResult
import com.example.gutenbergproject.utility.LoadType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repo: BookRepository
) : ViewModel() {

    // Holds the current UI state from the API call
    private val _state = MutableStateFlow<ApiResult<BookResponse>>(ApiResult.Loading)
    val state: StateFlow<ApiResult<BookResponse>> = _state

    // Pagination control
    private var currentPage = 1
    private val pageSize = 15
    var hasMore = true  // Indicates if more data is available
    var isLoading = false
        private set

    /**
     * Fetches books based on the given query and load type.
     * This function handles both initial and paginated data fetching.
     *
     * @param query The search term to query the API.
     * @param loadType Enum to determine if it's an initial load or a pagination request.
     */
    fun fetchBooks(query: String, loadType: LoadType) = viewModelScope.launch {
        if (isLoading) return@launch // Prevent parallel requests
        isLoading = true

        // Reset pagination and UI state on initial load
        if (loadType == LoadType.INITIAL) {
            currentPage = 1
            _state.value = ApiResult.Loading
        } else {
            // Advance to the next page on pagination
            currentPage++
        }

        // Perform the API call
        when (val res = repo.searchBooks(query, currentPage, pageSize)) {
            is ApiResult.Success -> {
                // Determine if more pages are available
                hasMore = res.data.results.size >= pageSize

                if (loadType == LoadType.INITIAL) {
                    // Directly set new data for initial load
                    _state.value = res
                } else {
                    // Merge new results with existing list for pagination
                    val currentData = (_state.value as? ApiResult.Success)?.data
                    val updatedList = currentData?.results.orEmpty() + res.data.results
                    val merged = res.data.copy(results = updatedList)
                    _state.value = ApiResult.Success(merged)
                }
            }

            // Handle error states
            is ApiResult.Error -> _state.value = res
            else -> {} // Do nothing for loading (already handled above)
        }

        isLoading = false
    }
}
