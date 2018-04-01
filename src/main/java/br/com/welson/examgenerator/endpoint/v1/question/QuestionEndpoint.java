package br.com.welson.examgenerator.endpoint.v1.question;

import br.com.welson.examgenerator.exception.ResourceNotFoundException;
import br.com.welson.examgenerator.persistence.model.Question;
import br.com.welson.examgenerator.persistence.repository.CourseRepository;
import br.com.welson.examgenerator.persistence.repository.QuestionRepository;
import br.com.welson.examgenerator.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Welson Teles on 3/23/2018
 */
@RestController
@RequestMapping("v1/professor/course/question")
@Api(description = "Operations related to course's question")
public class QuestionEndpoint {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final EndpointUtil endpointUtil;

    @Autowired
    public QuestionEndpoint(QuestionRepository questionRepository, CourseRepository courseRepository, EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Return a question based on it's id", response = Question.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable long id) {
        return new ResponseEntity<>(questionRepository.findById(id).orElseThrow(ResourceNotFoundException::new), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a specific question and return 200 OK without body")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        questionRepository.delete(questionRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Return a list of question related to course")
    @GetMapping(path = "list/{courseId}")
    public ResponseEntity<?> listQuestions(@ApiParam("Question title") @RequestParam(value = "title", defaultValue = "") String title, @PathVariable long courseId) {
        return new ResponseEntity<>(questionRepository.listByCourseAndTitle(courseId, title), HttpStatus.OK);
    }

    @ApiOperation(value = "Update question and return 200 OK without body")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Question question) {
        questionRepository.findById(question.getId()).orElseThrow(ResourceNotFoundException::new);
        questionRepository.save(question);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Save question and return 201 CREATED with body")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Question question) {
        courseRepository.findById(question.getCourse().getId()).orElseThrow(ResourceNotFoundException::new);
        question.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(questionRepository.save(question), HttpStatus.CREATED);
    }
}
