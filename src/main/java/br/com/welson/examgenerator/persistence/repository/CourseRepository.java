package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * @author Welson Teles on 3/19/2018
 */
@SuppressWarnings("ALL")
public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {

    @Query("select c from Course c where c.id = ?1 and c.professor = ?#{principal.professor}")
    @Override
    Optional<Course> findById(Long id);

    @Query("select c from Course c where c.name like %?1% and c.professor = ?#{principal.professor}")
    Iterable<Course> listByName(String name);

    @Query("select c from Course c where c = ?1 and c.professor = ?#{principal.professor}")
    Optional<Course> findOne(Course course);
}
