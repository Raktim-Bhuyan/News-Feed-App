package com.anurag.newsfeedapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurag.newsfeedapp.R
import com.anurag.newsfeedapp.adapters.NewsListAdapter
import com.anurag.newsfeedapp.databinding.FragmentHomeBinding
import com.anurag.newsfeedapp.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val customIntent by lazy { CustomTabsIntent.Builder().build() }
    private val mAdapter: NewsListAdapter by lazy {
        NewsListAdapter {
            customIntent.launchUrl(requireContext(), Uri.parse(it))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeNews()
    }

    private fun initRecyclerView() = binding.recyclerView.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = mAdapter
    }

    private fun observeNews() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newsResponse.observe(viewLifecycleOwner) {
                mAdapter.submitList(it.news)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_setting_fragment) {
            findNavController().navigate(R.id.nav_setting_fragment)
            return true
        } else if (item.itemId == R.id.nav_notification_fragment) {
            findNavController().navigate(R.id.nav_notification_fragment)
            return true
        } else return false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
