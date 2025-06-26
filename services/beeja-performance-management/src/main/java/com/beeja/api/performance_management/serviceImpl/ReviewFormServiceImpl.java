package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.exceptions.ResourceNotFoundException;
import com.beeja.api.performance_management.model.Questions;
import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.model.ReviewForm;
import com.beeja.api.performance_management.repository.QuestionsRepository;
import com.beeja.api.performance_management.repository.ReviewCycleRepository;
import com.beeja.api.performance_management.repository.ReviewFormRepository;
import com.beeja.api.performance_management.requests.CreateReviewFormRequest;
import com.beeja.api.performance_management.service.ReviewFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewFormServiceImpl implements ReviewFormService {
    @Autowired
    ReviewCycleRepository reviewCycleRepository;

    @Autowired
    ReviewFormRepository reviewFormRepository;

    @Autowired
    QuestionsRepository questionsRepository;

    @Override
    public void createReviewForm(String reviewCycleId, CreateReviewFormRequest request) {

        ReviewCycle reviewCycle = reviewCycleRepository.findById(reviewCycleId)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewCycle not found with id: " + reviewCycleId));

        ReviewForm form = new ReviewForm();
        form.setReviewCycleId(reviewCycleId);
        form.setDescription(request.getDescription());

        List<Questions> questions = request.getQuestionsList();
        if (questions != null && !questions.isEmpty()) {

            List<Questions> savedQuestions = questions.stream().map(q -> {
                Questions question = new Questions();
                question.setQuestion(q.getQuestion());
                question.setQuestionType(q.getQuestionType());
                question.setRequired(q.isRequired());
                return question;
            }).toList();

            savedQuestions = questionsRepository.saveAll(savedQuestions);
            form.setQuestionsList(savedQuestions);
        }
        reviewFormRepository.save(form);
        reviewCycle.setReviewFormId(form.getId());
        reviewCycleRepository.save(reviewCycle);
    }

    @Override
    public void updateReviewForm(String reviewFormId, CreateReviewFormRequest request) {

        ReviewForm reviewForm = reviewFormRepository.findById(reviewFormId)
                .orElseThrow(() -> new ResourceNotFoundException("ReviewForm not found with id: " + reviewFormId));

        if(!request.getQuestionsList().isEmpty()){
            reviewForm.setQuestionsList(request.getQuestionsList());
            reviewFormRepository.save(reviewForm);
        }
    }
}
