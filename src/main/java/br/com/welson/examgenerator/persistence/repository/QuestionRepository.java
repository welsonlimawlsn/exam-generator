package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.Course;
import br.com.welson.examgenerator.persistence.model.Question;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Welson Teles on 3/19/2018
 */
@SuppressWarnings("ALL")
public interface QuestionRepository extends CustomPagingAndSortingRepository<Question> {

    @Query("select q from Question q where q.course.id = ?1 and q.title like %?2% and q.professor = ?#{principal.professor} and q.enabled = true")
    Iterable<Question> listByCourseAndTitle(long courseId, String title);

    @Query("update Question q set q.enabled = false where q.course = ?1 and q.professor = ?#{principal.professor}")
    @Modifying
    void deleteAllByCourse(Course course);
}
