package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.Professor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProfessorRepository extends PagingAndSortingRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
}
