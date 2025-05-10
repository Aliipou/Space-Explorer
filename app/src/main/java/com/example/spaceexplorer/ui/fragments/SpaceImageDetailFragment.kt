package com.example.spaceexplorer.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.FragmentSpaceImageDetailBinding
import com.example.spaceexplorer.models.AstronomyPicture
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment for displaying detailed information about a specific space image
 */
class SpaceImageDetailFragment : Fragment() {

    private var _binding: FragmentSpaceImageDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SpaceViewModel by viewModel()
    private var currentPicture: AstronomyPicture? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpaceImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setHasOptionsMenu(true)
        observeViewModel()
        setupClickListeners()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_space_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareSpaceImage()
                true
            }
            R.id.action_open_in_browser -> {
                openInBrowser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun observeViewModel() {
        viewModel.selectedPicture.observe(viewLifecycleOwner) { picture ->
            currentPicture = picture
            displayPictureDetails(picture)
        }
    }
    
    private fun displayPictureDetails(picture: AstronomyPicture) {
        binding.apply {
            // Set title and date
            titleTextView.text = picture.title
            dateTextView.text = picture.date
            
            // Set full explanation
            explanationTextView.text = picture.explanation
            
            // Load image with Glide
            if (picture.isImage()) {
                // Use high-def URL if available, otherwise use standard URL
                val imageUrl = picture.hdUrl ?: picture.url
                
                Glide.with(this@SpaceImageDetailFragment)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.placeholder_image)
                    .into(spaceImageView)
                
                // Hide video player, show image
                spaceImageView.visibility = View.VISIBLE
                videoLayout.visibility = View.GONE
            } else {
                // It's a video, show video thumbnail and play button
                Glide.with(this@SpaceImageDetailFragment)
                    .load(R.drawable.ic_video_placeholder)
                    .into(videoThumbnailImageView)
                
                // Hide image, show video layout
                spaceImageView.visibility = View.GONE
                videoLayout.visibility = View.VISIBLE
            }
            
            // Set copyright information if available
            if (picture.copyright != null) {
                copyrightTextView.text = picture.getFormattedCopyright()
                copyrightTextView.visibility = View.VISIBLE
            } else {
                copyrightTextView.visibility = View.GONE
            }
            
            // Set source link
            sourceButton.text = getString(R.string.view_on_nasa)
        }
    }
    
    private fun setupClickListeners() {
        // Open NASA website when source button is clicked
        binding.sourceButton.setOnClickListener {
            openInBrowser()
        }
        
        // Play video when play button is clicked
        binding.playVideoButton.setOnClickListener {
            currentPicture?.let { picture ->
                if (!picture.isImage()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(picture.url))
                    startActivity(intent)
                }
            }
        }
    }
    
    private fun shareSpaceImage() {
        currentPicture?.let { picture ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, picture.title)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${picture.title}\n\n${picture.getShortExplanation(200)}\n\n" +
                        "View on NASA: ${picture.url}"
                )
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_space_image)))
        }
    }
    
    private fun openInBrowser() {
        currentPicture?.let { picture ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(picture.url))
            startActivity(intent)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}