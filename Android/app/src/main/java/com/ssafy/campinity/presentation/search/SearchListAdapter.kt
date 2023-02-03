package com.ssafy.campinity.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.campinity.databinding.ItemSearchListBinding
import com.ssafy.campinity.domain.entity.search.CampsiteBriefInfo

class SearchListAdapter(
    private val campsites: List<CampsiteBriefInfo>,
    private val navigationToCampsiteDetailFragment: (String) -> Unit,
    private val navigationToSearchPostboxFragment: () -> Unit
) : RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    private lateinit var binding: ItemSearchListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        binding = ItemSearchListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        with(campsites[position]) {
            holder.bind(this)
            holder.initListener(this)
            holder.initRecyclerView()
        }
    }

    override fun getItemCount(): Int = campsites.size

    inner class SearchListViewHolder(private val binding: ItemSearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CampsiteBriefInfo) {
            binding.item = item
        }

        fun initListener(item: CampsiteBriefInfo) {
            binding.btnPostbox.setOnClickListener {
                navigationToSearchPostboxFragment()
            }

            binding.rlCampsite.setOnClickListener {
                navigationToCampsiteDetailFragment(item.campsiteId)
            }
        }

        fun initRecyclerView() {
            binding.rvCampsiteImage.apply {
                layoutManager = LinearLayoutManager(
                    binding.rvCampsiteImage.context,
                    RecyclerView.HORIZONTAL,
                    false
                )
                adapter = CampsiteBriefImageAdapter(campsites[adapterPosition].images)
            }
        }
    }
}