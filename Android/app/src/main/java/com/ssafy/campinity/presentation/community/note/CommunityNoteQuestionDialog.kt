package com.ssafy.campinity.presentation.community.note

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import com.ssafy.campinity.databinding.DialogWriteNoteQuestionBinding

class CommunityNoteQuestionDialog(
    context: Context,
    private val communityNoteDialogInterface: CommunityNoteDialogInterface
) : Dialog(context) {

    private lateinit var binding: DialogWriteNoteQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogWriteNoteQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        binding.apply {
            tvMakeEventNoteMarker.setOnClickListener {
                communityNoteDialogInterface.postNote(
                    "47f55bf6-19e2-42a2-a5d8-a1a4552dd845",
                    etInputMakeQuestion.text.toString()
                )
                dismiss()
            }
            tvCancelEventNoteMarker.setOnClickListener { dismiss() }
        }
    }
}