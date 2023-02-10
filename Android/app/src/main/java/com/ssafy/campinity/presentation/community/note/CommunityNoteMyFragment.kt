package com.ssafy.campinity.presentation.community.note

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.campinity.ApplicationClass
import com.ssafy.campinity.R
import com.ssafy.campinity.databinding.FragmentCommunityNoteMyBinding
import com.ssafy.campinity.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityNoteMyFragment :
    BaseFragment<FragmentCommunityNoteMyBinding>(R.layout.fragment_community_note_my) {

    private val communityNoteViewModel by activityViewModels<CommunityNoteViewModel>()
    private val communityNoteListAdapter by lazy {
        CommunityNoteListAdapter(this::getPost)
    }

    override fun initView() {
        initNote()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        communityNoteViewModel.getNoteMyQuestions(
            ApplicationClass.preferences.userRecentCampsiteId.toString()
        )
    }

    private fun initObserver() {
        communityNoteViewModel.noteMyQuestions.observe(viewLifecycleOwner) { response ->
            response?.let { communityNoteListAdapter.setNote(it) }
        }
    }

    private fun initNote() {
        binding.rvCommunityMyNote.apply {
            adapter = communityNoteListAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private fun getPost(questionId: String) {
        navigate(
            CommunityNoteFragmentDirections.actionCommunityNoteFragmentToCommunityNoteDetailFragment(
                questionId
            )
        )
    }
}