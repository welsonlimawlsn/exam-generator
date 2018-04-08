package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.Course;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Welson Teles on 3/19/2018
 */
@SuppressWarnings("ALL")
public interface CourseRepository extends CustomPagingAndSortingRepository<Course> {

    @Query("select c from Course c where c.name like %?1% and c.professor = ?#{principal.professor} and c.enabled = true")
    Iterable<Course> listByName(String name);
}
