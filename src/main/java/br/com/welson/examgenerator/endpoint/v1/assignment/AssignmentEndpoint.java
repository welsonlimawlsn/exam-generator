package br.com.welson.examgenerator.endpoint.v1.assignment;

import br.com.welson.examgenerator.exception.ResourceNotFoundException;
import br.com.welson.examgenerator.persistence.model.Assignment;
import br.com.welson.examgenerator.persistence.repository.AssignmentRepository;
import br.com.welson.examgenerator.persistence.repository.CourseRepository;
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
 * @author Welson Teles on 4/6/2018
 */
@RestController
@RequestMapping("v1/professor/course/assignment")
@Api(description = "Operations related to courses' assignment")
public class AssignmentEndpoint {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EndpointUtil endpointUtil;

    @Autowired
    public AssignmentEndpoint(AssignmentRepository assignmentRepository, CourseRepository courseRepository, EndpointUtil endpointUtil) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Return an assignment based on it's id", response = Assignment.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable long id) {
        return new ResponseEntity<>(assignmentRepository.findById(id).orElseThrow(ResourceNotFoundException::new), HttpStatus.OK);
    }

    @ApiOperation(value = "Return a list of assignment related to course", response = Assignment[].class)
    @GetMapping(path = "list/{courseId}")
    public ResponseEntity<?> listAssignments(@PathVariable long courseId, @ApiParam(value = "Assignment title", defaultValue = "") @RequestParam(value = "title", defaultValue = "") String title) {
        return new ResponseEntity<>(assignmentRepository.listByCourseAndTitle(courseId, title), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a specific assignment and return 200 OK without body")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        assignmentRepository.delete(assignmentRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Update assignment and return 200 OK without body")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Assignment assignment) {
        assignmentRepository.findById(assignment.getId()).orElseThrow(ResourceNotFoundException::new);
        assignmentRepository.save(assignment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Create assignment and return 201 CREATED", response = Assignment.class)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Assignment assignment) {
        courseRepository.findById(assignment.getCourse().getId()).orElseThrow(ResourceNotFoundException::new);
        assignment.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(assignmentRepository.save(assignment), HttpStatus.CREATED);
    }
}
