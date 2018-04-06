package br.com.welson.examgenerator.endpoint.v1.choice;

import br.com.welson.examgenerator.exception.ResourceNotFoundException;
import br.com.welson.examgenerator.persistence.model.Choice;
import br.com.welson.examgenerator.persistence.model.Question;
import br.com.welson.examgenerator.persistence.repository.ChoiceRepository;
import br.com.welson.examgenerator.persistence.repository.QuestionRepository;
import br.com.welson.examgenerator.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Welson Teles on 4/1/2018
 */
@RestController
@RequestMapping("v1/professor/course/question/choice")
@Api(description = "Operations related to questions' choice")
public class ChoiceEndpoint {

    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final EndpointUtil endpointUtil;

    @Autowired
    public ChoiceEndpoint(QuestionRepository questionRepository, ChoiceRepository choiceRepository, EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Return a list of choices related to the questionId", response = Choice[].class)
    @GetMapping(path = "list/{questionId}")
    public ResponseEntity<?> listChoices(@PathVariable long questionId) {
        return new ResponseEntity<>(choiceRepository.findAllByQuestionId(questionId), HttpStatus.OK);
    }

    @ApiOperation(value = "Return a choice based on it's id", response = Choice.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getChoice(@PathVariable long id) {
        return new ResponseEntity<>(choiceRepository.findById(id).orElseThrow(ResourceNotFoundException::new), HttpStatus.OK);
    }

    @ApiOperation(value = "Save choice and return 201 CREATED with body", notes = "If this choice is correct answer is true, all other choices' correct answer related to this question will be updated to false", response = Choice.class)
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@Valid @RequestBody Choice choice) {
        questionRepository.findById(choice.getQuestion().getId()).orElseThrow(ResourceNotFoundException::new);
        choice.setProfessor(endpointUtil.extractProfessorFromToken());
        choice = choiceRepository.save(choice);
        updateChangingOtherChoicesCorrectAnswerToFalse(choice);
        return new ResponseEntity<>(choice, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update choice and return 200 Ok without body", notes = "If this choice is correct answer is true, all other choices' correct answer related to this question will be updated to false")
    @PutMapping
    @Transactional
    public ResponseEntity<?> update(@Valid @RequestBody Choice choice) {
        choiceRepository.findById(choice.getId()).orElseThrow(ResourceNotFoundException::new);
        choice = choiceRepository.save(choice);
        updateChangingOtherChoicesCorrectAnswerToFalse(choice);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a specific choice and return 200 OK without body")
    @DeleteMapping("/{choiceId}")
    public ResponseEntity<?> delete(@PathVariable long choiceId) {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(ResourceNotFoundException::new);
        choiceRepository.delete(choice);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void updateChangingOtherChoicesCorrectAnswerToFalse(Choice choice) {
        if (choice.isCorrectAnswer()) {
            choiceRepository.updateAllOtherChoicesCorrectAnswerToFalse(choice, choice.getQuestion());
        }
    }
}
