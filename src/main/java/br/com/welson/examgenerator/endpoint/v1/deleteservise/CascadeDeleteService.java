package br.com.welson.examgenerator.endpoint.v1.deleteservise;

import br.com.welson.examgenerator.persistence.model.Course;
import br.com.welson.examgenerator.persistence.model.Question;
import br.com.welson.examgenerator.persistence.repository.AssignmentRepository;
import br.com.welson.examgenerator.persistence.repository.ChoiceRepository;
import br.com.welson.examgenerator.persistence.repository.CourseRepository;
import br.com.welson.examgenerator.persistence.repository.QuestionRepository;
import org.springframework.stereotype.Service;

/**
 * @author Welson Teles on 4/8/2018
 */
@Service
public class CascadeDeleteService {

    private final ChoiceRepository choiceRepository;
    private final QuestionRepository questionRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public CascadeDeleteService(ChoiceRepository choiceRepository, QuestionRepository questionRepository, AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.choiceRepository = choiceRepository;
        this.questionRepository = questionRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    public void deleteQuestionAndAllRelatedEntities(Question question) {
        choiceRepository.deleteAllByQuestion(question);
        questionRepository.delete(question);
    }

    public void deleteCourseAndAllRelatedEntities(Course course) {
        choiceRepository.deleteAllByCourse(course);
        questionRepository.deleteAllByCourse(course);
        assignmentRepository.deleteAllByCourse(course);
        courseRepository.delete(course);
    }
}
