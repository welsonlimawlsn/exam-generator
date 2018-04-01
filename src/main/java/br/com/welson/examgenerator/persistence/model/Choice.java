package br.com.welson.examgenerator.persistence.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Welson Teles on 4/1/2018
 */
@Entity
public class Choice extends AbstractEntity {

    @NotEmpty(message = "The field title cannot be empty")
    @ApiModelProperty(notes = "The title of the choice")
    private String title;

    @NotNull(message = "The field correctAnswer must be true or false")
    @ApiModelProperty(notes = "Correct answer for the associated question, you can have only one correct answer per question")
    private boolean correctAnswer;
    @ManyToOne(optional = false)
    private Question question;
    @ManyToOne(optional = false)
    private Professor professor;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public static final class ChoiceBuilder {
        private Choice choice;

        private ChoiceBuilder() {
            choice = new Choice();
        }

        public static ChoiceBuilder newChoice() {
            return new ChoiceBuilder();
        }

        public ChoiceBuilder id(Long id) {
            choice.setId(id);
            return this;
        }

        public ChoiceBuilder enabled(boolean enabled) {
            choice.setEnabled(enabled);
            return this;
        }

        public ChoiceBuilder title(String title) {
            choice.setTitle(title);
            return this;
        }

        public ChoiceBuilder correctAnswer(boolean correctAnswer) {
            choice.setCorrectAnswer(correctAnswer);
            return this;
        }

        public ChoiceBuilder question(Question question) {
            choice.setQuestion(question);
            return this;
        }

        public ChoiceBuilder professor(Professor professor) {
            choice.setProfessor(professor);
            return this;
        }

        public Choice build() {
            return choice;
        }
    }
}
