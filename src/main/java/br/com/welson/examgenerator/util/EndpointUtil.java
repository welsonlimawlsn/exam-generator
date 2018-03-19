package br.com.welson.examgenerator.util;

import br.com.welson.examgenerator.persistence.model.ApplicationUser;
import br.com.welson.examgenerator.persistence.model.Professor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author Welson Teles on 3/19/2018
 */
@Service
public class EndpointUtil {

    public Professor extractProfessorFromToken() {
        return ((ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getProfessor();
    }
}
