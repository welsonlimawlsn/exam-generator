package br.com.welson.examgenerator.endpoint.v1.course;

import br.com.welson.examgenerator.endpoint.v1.ProfessorEndpointTest;
import br.com.welson.examgenerator.persistence.model.Course;
import br.com.welson.examgenerator.persistence.repository.CourseRepository;
import br.com.welson.examgenerator.persistence.repository.ProfessorRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CourseEndpointTest {

    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Course course = mockCourse();

    public static Course mockCourse() {
        return Course.CourseBuilder.newCourse()
                .id(1L)
                .name("Java")
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
        headers.add("Authorization", "132456789");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Before
    public void setup() {
        BDDMockito.when(courseRepository.findById(course.getId())).thenReturn(Optional.ofNullable(course));
//        BDDMockito.when(courseRepository.findById(-1L)).thenThrow(new ResourceNotFoundException());
        BDDMockito.when(courseRepository.listByName("")).thenReturn(Collections.singletonList(course));
        BDDMockito.when(courseRepository.listByName("java")).thenReturn(Collections.singletonList(course));
    }

    @Test
    public void getCourseByIdWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/1", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listCoursesWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/list", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listAllCoursesShouldReturn200() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/list", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getCourseByIdWithoutIdShouldReturn400() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void getCourseByIdWhenCourseIdDoesNotExistsShouldReturn404() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/-1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void getCourseByIdWhenCourseExistsShouldReturn200() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteCourseWhenIdExistsShouldReturn200() {
        long id = 1;
        BDDMockito.doNothing().when(courseRepository).deleteById(id);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/{id}", HttpMethod.DELETE, professorHeader, String.class, id);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    //
//    @Test
//    public void deleteCourseWhenIdDoesNotExistsShouldReturn404() {
//
//    }

    @Test
    public void createCourseWhenNameIsNullShouldReturn400() {
        Course course = courseRepository.findById(1L).get();
        course.setName(null);
        Assertions.assertThat(createCourse(course).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createCourseWhenEverythingIsRightShouldReturn201() {
        Course course = courseRepository.findById(1L).get();
        Assertions.assertThat(createCourse(course).getStatusCodeValue()).isEqualTo(201);
    }

    private ResponseEntity<String> createCourse(Course course) {
        BDDMockito.when(courseRepository.save(course)).thenReturn(course);
        return testRestTemplate.exchange("/v1/professor/course/", HttpMethod.POST, new HttpEntity<>(course, professorHeader.getHeaders()), String.class);
    }
}