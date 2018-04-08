package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.Assignment;
import br.com.welson.examgenerator.persistence.model.Course;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Welson Teles on 4/6/2018
 */
@SuppressWarnings("ALL")
public interface AssignmentRepository extends CustomPagingAndSortingRepository<Assignment> {

    @Query("select a from Assignment a where a.course.id = ?1 and a.title like %?2% and a.professor = ?#{principal.professor} and a.enabled = true")
    Iterable<Assignment> listByCourseAndTitle(long courseId, String title);

    @Query("update Assignment a set a.enabled = false where a.course = ?1 and a.professor = ?#{principal.professor}")
    @Modifying
    void deleteAllByCourse(Course course);
}
