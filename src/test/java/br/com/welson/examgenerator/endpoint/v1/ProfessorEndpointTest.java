package br.com.welson.examgenerator.endpoint.v1;

import br.com.welson.examgenerator.persistence.model.Professor;

import static org.junit.Assert.*;

public class ProfessorEndpointTest {

    public static Professor mockProfessor() {
        return Professor.ProfessorBuilder.newProfessor()
                .id(1L)
                .name("Welson")
                .email("welsonlimawlsn@gmail.com")
                .build();
    }

}