package br.com.welson.examgenerator.endpoint.v1.assignment;

import br.com.welson.examgenerator.endpoint.v1.ProfessorEndpointTest;
import br.com.welson.examgenerator.endpoint.v1.course.CourseEndpointTest;
import br.com.welson.examgenerator.persistence.model.Assignment;
import br.com.welson.examgenerator.persistence.model.Course;
import br.com.welson.examgenerator.persistence.repository.AssignmentRepository;
import br.com.welson.examgenerator.persistence.repository.CourseRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AssignmentEndpointTest {

    @MockBean
    private AssignmentRepository assignmentRepository;
    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Assignment assignment = mockAssignment();

    private Assignment mockAssignment() {
        return Assignment.AssignmentBuilder.newAssignment()
                .id(1L)
                .title("The Java Exam  from Hell")
                .course(CourseEndpointTest.mockCourse())
                .professor(ProfessorEndpointTest.mockProfessor())
                .build();
    }

    @Before
    public void configProfessorHeader() {
        String body = "{\"username\":\"welson\",\"password\":\"welson\"}";
        HttpHeaders headers = testRestTemplate.postForEntity("/login", body, String.class).getHeaders();
        this.professorHeader = new HttpEntity<>(headers);
    }

    @Before
    public void configWrongHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "123456");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Before
    public void setUp() {
        BDDMockito.when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
        BDDMockito.when(assignmentRepository.listByCourseAndTitle(assignment.getCourse().getId(), "")).thenReturn(Collections.singletonList(assignment));
        BDDMockito.when(assignmentRepository.listByCourseAndTitle(assignment.getCourse().getId(), "The Java Exam  from Hell")).thenReturn(Collections.singletonList(assignment));
        BDDMockito.when(courseRepository.findById(assignment.getCourse().getId())).thenReturn(Optional.ofNullable(assignment.getCourse()));
    }

    @Test
    public void getAssignmentByIdWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/1", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listAssignmentsByCourseAndTitleWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/list/1/?title=", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listAllAssignmentByCourseAndTitleWhenTitleDoesNotExistsShouldReturnEmptyList() {
        ResponseEntity<List<Assignment>> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/list/1/?title=what", HttpMethod.GET, professorHeader, new ParameterizedTypeReference<List<Assignment>>() {
        });
        Assertions.assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void listAllAssignmentByCourseAndTitleWhenTitleExistsShouldReturn200() {
        ResponseEntity<List<Assignment>> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/list/1/?title=The Java Exam  from Hell", HttpMethod.GET, professorHeader, new ParameterizedTypeReference<List<Assignment>>() {
        });
        Assertions.assertThat(exchange.getBody()).isNotEmpty();
    }

    @Test
    public void getAssignmentByIdWithoutIdShouldReturn400() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void getAssignmentByIdWhenCourseIdDoesNotExistsShouldReturn404() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/-1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void getAssignmentByIdWhenAssignmentIdDoesNotExistsShouldReturn404() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/-1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void getAssignmentByIdWhenAssignmentIdExistsShouldReturn200() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteAssignmentByIdWhenAssignmentIdExistsShouldReturn200() {
        long id = 1L;
        BDDMockito.doNothing().when(assignmentRepository).deleteById(id);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/{id}", HttpMethod.DELETE, professorHeader, String.class, id);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteAssignmentByIdWhenAssignmentIdDoesNotExistsShouldReturn404() {
        long id = -1L;
        BDDMockito.doNothing().when(assignmentRepository).deleteById(id);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/assignment/{id}", HttpMethod.DELETE, professorHeader, String.class, id);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void createAssignmentWhenTitleIsNullShouldReturn400() {
        Assignment assignment = assignmentRepository.findById(1L).get();
        assignment.setTitle(null);
        Assertions.assertThat(createAssignment(assignment).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createAssignmentWhenCourseDoesNotExistsShouldReturn404() {
        Assignment assignment = assignmentRepository.findById(1L).get();
        assignment.setCourse(new Course());
        Assertions.assertThat(createAssignment(assignment).getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void createAssignmentWhenEverythingIsRightShouldReturn201() {
        Assignment assignment = assignmentRepository.findById(1L).get();
        assignment.setId(null);
        Assertions.assertThat(createAssignment(assignment).getStatusCodeValue()).isEqualTo(201);
    }

    private ResponseEntity<?> createAssignment(Assignment assignment) {
        BDDMockito.when(assignmentRepository.save(assignment)).thenReturn(assignment);
        return testRestTemplate.exchange("/v1/professor/course/assignment/", HttpMethod.POST, new HttpEntity<>(assignment, professorHeader.getHeaders()), String.class);
    }


}