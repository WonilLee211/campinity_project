package com.ssafy.campinity.core.service.impl;

import com.ssafy.campinity.core.dto.AnswerReqDTO;
import com.ssafy.campinity.core.dto.AnswerResDTO;
import com.ssafy.campinity.core.entity.answer.Answer;
import com.ssafy.campinity.core.entity.member.Member;
import com.ssafy.campinity.core.entity.question.Question;
import com.ssafy.campinity.core.repository.answer.AnswerRepository;
import com.ssafy.campinity.core.repository.member.MemberRepository;
import com.ssafy.campinity.core.repository.question.QuestionRepository;
import com.ssafy.campinity.core.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    private final QuestionRepository questionRepository;

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public AnswerResDTO createAnswer(AnswerReqDTO answerReqDTO, int requestMemberId) {
        Question question = questionRepository.findByUuidAndExpiredIsFalse(answerReqDTO.getQuestionId()).orElseThrow(IllegalArgumentException::new);
        Member member = memberRepository.findMemberByIdAndExpiredIsFalse(requestMemberId).orElseThrow(IllegalArgumentException::new);

        Answer answer = Answer.builder().uuid(UUID.randomUUID()).question(question).member(member).content(answerReqDTO.getContent()).build();

        question.addAnswer(answer);
        Answer savedAnswer = answerRepository.save(answer);

        return AnswerResDTO.builder().answer(savedAnswer).build();
    }
}
