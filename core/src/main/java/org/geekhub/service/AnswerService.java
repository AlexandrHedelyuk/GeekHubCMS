package org.geekhub.service;

import org.geekhub.hibernate.entity.Answer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by helldes on 15.05.2015.
 */
@Component
public interface AnswerService {
    public List<Answer> getAnswersByQuestion(int questionId);
    Object read(int answerId);
    void create(int questionId, String answerText, Boolean answerRight);
    void delete(int answerId);
}