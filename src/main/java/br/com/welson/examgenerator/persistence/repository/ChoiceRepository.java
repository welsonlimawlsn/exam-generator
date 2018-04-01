package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.Choice;
import br.com.welson.examgenerator.persistence.model.Question;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Welson Teles on 4/1/2018
 */
public interface ChoiceRepository extends CustomPagingAndSortingRepository<Choice> {

    @Query("select c from Choice c where c.question.id = ?1 and c.professor = ?#{principal.professor} and c.enabled = true")
    Iterable<Choice> findAllByQuestionId(long questionId);

    @Query("update Choice c set c.correctAnswer = false where c <> ?1 and c.question = ?2 and c.professor = ?#{principal.professor} and c.enabled = true")
    @Modifying
    void updateAllOtherChoicesCorrectAnswerToFalse(Choice choice, Question question);
}
