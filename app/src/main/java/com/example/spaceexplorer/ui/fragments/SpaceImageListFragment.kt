package com.example.spaceexplorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.FragmentSpaceImageListBinding
import com.example.spaceexplorer.models.AstronomyPicture
import com.example.spaceexplorer.ui.adapters.SpaceImageAdapter
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment for displaying a list of space images
 */
class SpaceImageListFragment : Fragment() {

    private var _binding: FragmentSpaceImageListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SpaceViewModel by viewModel()
    private lateinit var adapter: SpaceImageAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaceImageListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setHasOptionsMenu(true)
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_space_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.loadRandomAstronomyPictures()
                true
            }
            R.id.action_recent -> {
                viewModel.loadRecentAstronomyPictures()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun setupRecyclerView() {
        adapter = SpaceImageAdapter { picture ->
            navigateToDetail(picture)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SpaceImageListFragment.adapter
        }
    }
    
    private fun observeViewModel() {
        // Observe astronomy pictures
        viewModel.astronomyPictures.observe(viewLifecycleOwner) { pictures ->
            adapter.submitList(pictures)
            binding.emptyView.isVisible = pictures.isEmpty()
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRandomAstronomyPictures()
        }
    }
    
    private fun navigateToDetail(picture: AstronomyPicture) {
        viewModel.selectPicture(picture)
        findNavController().navigate(
            SpaceImageListFragmentDirections.actionSpaceImageListFragmentToSpaceImageDetailFragment()
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}