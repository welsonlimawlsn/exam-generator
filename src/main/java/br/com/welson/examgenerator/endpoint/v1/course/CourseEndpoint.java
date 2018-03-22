package br.com.welson.examgenerator.endpoint.v1.course;

import br.com.welson.examgenerator.exception.ResourceNotFoundException;
import br.com.welson.examgenerator.persistence.model.Course;
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
 * @author Welson Teles on 3/19/2018
 */

@RestController
@RequestMapping("v1/professor/course")
@Api(description = "Operations related to professor's course")
public class CourseEndpoint {

    private final CourseRepository courseRepository;
    private final EndpointUtil endpointUtil;

    @Autowired
    public CourseEndpoint(CourseRepository courseRepository, EndpointUtil endpointUtil) {
        this.courseRepository = courseRepository;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Return a course based on it's id", response = Course.class)
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable long id) {
        return new ResponseEntity<>(courseRepository.findById(id).orElseThrow(ResourceNotFoundException::new), HttpStatus.OK);
    }

    @ApiOperation(value = "Return a list of courses related to professor", response = Course.class)
    @GetMapping(path = "/list")
    public ResponseEntity<?> listCourses(@ApiParam("Course name") @RequestParam(value = "name", defaultValue = "") String name) {
        return new ResponseEntity<>(courseRepository.listByName(name), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a course and return 200 OK without body")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable long id) {
        courseRepository.delete(courseRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @ApiOperation(value = "Update course and return 200 OK without body")
    @PutMapping
    public ResponseEntity<?> updateCourse(@Valid @RequestBody Course course) {
        courseRepository.findById(course.getId()).orElseThrow(ResourceNotFoundException::new);
        courseRepository.save(course);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Create course and return the course created", response = Course.class, code = 201)
    @PostMapping
    public ResponseEntity<?> createCourse(@Valid @RequestBody Course course) {
        course.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(courseRepository.save(course), HttpStatus.CREATED);
    }
}
