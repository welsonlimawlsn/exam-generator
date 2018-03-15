package br.com.welson.examgenerator.persistence.repository;

import br.com.welson.examgenerator.persistence.model.ApplicationUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByUsername(String username);
}
