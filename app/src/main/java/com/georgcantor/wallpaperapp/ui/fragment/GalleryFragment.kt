package com.georgcantor.wallpaperapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.FragmentGalleryBinding
import com.georgcantor.wallpaperapp.model.remote.response.LoadableResult
import com.georgcantor.wallpaperapp.ui.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val binding by viewBinding(FragmentGalleryBinding::bind)
    private val viewModel: GalleryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        val request = (findNavController().currentDestination?.label).toString()

        val galleryAdapter = GalleryAdapter { pic ->
            requireActivity().startActivity<DetailActivity> { putExtra(PIC_EXTRA, pic) }
        }

        galleryAdapter.addLoadStateListener { state ->
            when (val stateRefresh = state.refresh) {
                is LoadState.Error -> {
                    stateViewFlipper.setStateFromResult(LoadableResult.failure<Unit>(stateRefresh.error))
                }
                is LoadState.Loading -> stateViewFlipper.setStateLoading()
                is LoadState.NotLoading -> {
                    stateViewFlipper.setStateData()
                    if (galleryAdapter.itemCount == 0 && state.append.endOfPaginationReached) {
                        stateViewFlipper.setEmptyStateWithTitles(
                            getString(R.string.something_went_wrong),
                            getString(R.string.something_went_wrong),
                            getString(R.string.something_went_wrong)
                        )
                    }
                }
            }
        }

        picturesRecycler.apply {
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = galleryAdapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getPicListStream(request).collectLatest {
                galleryAdapter.submitData(it)
            }
        }
        stateViewFlipper.setEmptyMethod { Toast.makeText(requireContext(), "e", Toast.LENGTH_SHORT).show() }
        stateViewFlipper.setRetryMethod { Toast.makeText(requireContext(), "r", Toast.LENGTH_SHORT).show() }
    }
}