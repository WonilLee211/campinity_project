package com.ssafy.campinity.presentation.community.note

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.campinity.ApplicationClass
import com.ssafy.campinity.R
import com.ssafy.campinity.databinding.FragmentCommunityNoteDetailBinding
import com.ssafy.campinity.presentation.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityNoteDetailFragment :
    BaseFragment<FragmentCommunityNoteDetailBinding>(R.layout.fragment_community_note_detail),
    CommunityNoteDialogInterface {

    private val args by navArgs<CommunityNoteDetailFragmentArgs>()
    private val communityNoteViewModel by activityViewModels<CommunityNoteViewModel>()
    private val communityNoteQuestionAnswerAdapter by lazy {
        CommunityNoteQuestionAnswerAdapter()
    }

    override fun initView() {
        initObserver()
        initNoteDetail()
        initListener()
    }

    private fun initObserver() {
        communityNoteViewModel.noteQuestionDetail.observe(viewLifecycleOwner) { response ->
            response?.let {
                binding.tvNoteQuestionContent.text = it.content
                communityNoteQuestionAnswerAdapter.setAnswer(it.answers)
            }
        }
    }

    private fun initListener() {
        binding.apply {
            ivCloseNoteDetail.setOnClickListener {
                popBackStack()
            }
            tvMakeAnswer.setOnClickListener {
                if (ApplicationClass.preferences.isUserCanAnswer == true.toString()) {
                    CommunityNoteAnswerDialog(
                        requireContext(),
                        this@CommunityNoteDetailFragment,
                        args.questionId,
                        binding.tvNoteQuestionContent.text.toString()
                    ).show()
                } else {
                    showToast("캠핑장을 구독중이어야 답변이 가능합니다.")
                }
            }
        }
    }

    private fun initNoteDetail() {
        binding.rvNoteDetailAnswer.apply {
            adapter = communityNoteQuestionAnswerAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        communityNoteViewModel.getNoteQuestionDetail(args.questionId)
    }

    override fun postNote(id: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (communityNoteViewModel.postNoteAnswer(content, id)) {
                withContext(Dispatchers.Main) {
                    showToast("답변이 등록되었습니다.")
                    communityNoteViewModel.getNoteQuestionDetail(args.questionId)
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("답변 등록에 실패하였습니다.")
                }
            }
        }
    }
}