package br.com.welson.examgenerator.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
public class Professor extends AbstractEntity {

    @NotEmpty(message = "The field name cannot be empty")
    private String name;
    @Email(message = "This email is not valid")
    @NotEmpty(message = "The field email cannot be empty")
    @Column(unique = true)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static final class ProfessorBuilder {
        private Professor professor;

        private ProfessorBuilder() {
            professor = new Professor();
        }

        public static ProfessorBuilder newProfessor() {
            return new ProfessorBuilder();
        }

        public ProfessorBuilder name(String name) {
            professor.setName(name);
            return this;
        }

        public ProfessorBuilder id(Long id) {
            professor.setId(id);
            return this;
        }

        public ProfessorBuilder email(String email) {
            professor.setEmail(email);
            return this;
        }

        public Professor build() {
            return professor;
        }
    }
}
