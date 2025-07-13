package com.example.gutenbergproject.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gutenbergproject.adapters.CategoryListAdapter
import com.example.gutenbergproject.databinding.FragmentHomeBinding
import com.example.gutenbergproject.repositories.CategoryRepository
import com.example.gutenbergproject.utility.capitalizeFirst
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryRecyclerView()
    }

    /**
     * Initializes and binds the RecyclerView with category data
     * and sets up navigation using Safe Args.
     */
    private fun setupCategoryRecyclerView() {
        val categories = CategoryRepository.getCategories()

        categoryAdapter = CategoryListAdapter(categories) { selectedItem ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToCategoryFragment(selectedItem.title.capitalizeFirst()) // optional: format title
            findNavController().navigate(action)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * Prevent memory leaks by clearing binding when view is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
