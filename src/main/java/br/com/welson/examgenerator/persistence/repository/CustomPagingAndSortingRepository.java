package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.exception.ResourceNotFoundException;
import br.com.welson.examgenerator.persistence.model.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Welson Teles on 3/22/2018
 */
@NoRepositoryBean
public interface CustomPagingAndSortingRepository<T extends AbstractEntity> extends PagingAndSortingRepository<T, Long> {

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Iterable<T> findAll(Sort sort);

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Page<T> findAll(Pageable pageable);

    @Override
    @Query("select e from #{#entityName} e where e.id = ?1 and e.professor = ?#{principal.professor} and e.enabled = true")
    Optional<T> findById(Long id);

    @Override
    default boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    default Iterable<T> findAllById(Iterable<Long> longs) {
        List<T> list = new ArrayList<>();
        longs.forEach(entity -> list.add(findById(entity).orElseThrow(ResourceNotFoundException::new)));
        return list;
    }

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Iterable<T> findAll();

    @Override
    @Query("select count(e) from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    long count();

    @Override
    @Modifying
    @Query("update #{#entityName} e set e.enabled = false where e.id = ?1 and e.professor = ?#{principal.professor}")
    void deleteById(Long id);

    @Override
    default void delete(T entity) {
        deleteById(entity.getId());
    }

    @Override
    default void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(entity -> deleteById(entity.getId()));
    }

    @Override
    @Modifying
    @Query("update #{#entityName} e set e.enabled = false where e.professor = ?#{principal.professor}")
    void deleteAll();
}
