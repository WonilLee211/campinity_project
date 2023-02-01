package com.ssafy.campinity.domain.repository

import com.ssafy.campinity.data.remote.Resource
import com.ssafy.campinity.data.remote.datasource.note.NoteQuestionAnswerRequest
import com.ssafy.campinity.domain.entity.community.NoteDetail
import com.ssafy.campinity.domain.entity.community.NoteQuestionAnswer
import com.ssafy.campinity.domain.entity.community.NoteQuestionTitle

interface NoteRepository {

    suspend fun getQuestions(campsiteId: String): Resource<List<NoteQuestionTitle>>

    suspend fun getMyQuestions(campsiteId: String): Resource<List<NoteQuestionTitle>>

    suspend fun getPostQuestion(
        campsiteId: String,
        content: String
    ): Resource<NoteQuestionTitle>

    suspend fun getQuestionsDetail(questionId: String): Resource<NoteDetail>

    suspend fun getPostAnswer(noteQuestionAnswerRequest: NoteQuestionAnswerRequest): Resource<NoteQuestionAnswer>
}