package br.com.welson.examgenerator.endpoint.v1.question;

import br.com.welson.examgenerator.endpoint.v1.ProfessorEndpointTest;
import br.com.welson.examgenerator.endpoint.v1.course.CourseEndpointTest;
import br.com.welson.examgenerator.persistence.model.Question;
import br.com.welson.examgenerator.persistence.repository.QuestionRepository;
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
public class QuestionEndpointTest {
    @MockBean
    private QuestionRepository questionRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Question question = mockQuestion();

    private static Question mockQuestion() {
        return Question.QuestionBuilder.newQuestion()
                .id(1L)
                .title("What is class?")
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
        headers.add("Authorization", "132456789");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Before
    public void setup() {
        BDDMockito.when(questionRepository.findById(question.getId())).thenReturn(Optional.ofNullable(question));
//        BDDMockito.when(questionRepository.findById(-1L)).thenThrow(new ResourceNotFoundException());
        BDDMockito.when(questionRepository.listByCourseAndTitle(question.getCourse().getId(), "")).thenReturn(Collections.singletonList(question));
        BDDMockito.when(questionRepository.listByCourseAndTitle(question.getCourse().getId(), "What is class?")).thenReturn(Collections.singletonList(question));
    }

    @Test
    public void getQuestionByIdWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/1", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listQuestionsByCourseWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/list/1/?title=", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listAllQuestionsByCourseWhenTitleDoesNotExistsShouldReturnEmptyList() {
        ResponseEntity<List<String>> exchange = testRestTemplate.exchange("/v1/professor/course/question/list/1/?title=fadawdwad", HttpMethod.GET, professorHeader, new ParameterizedTypeReference<List<String>>() {
        });
        Assertions.assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void listAllQuestionsByCourseWhenTitleExistsShouldReturn200() {
        ResponseEntity<List<String>> exchange = testRestTemplate.exchange("/v1/professor/course/question/list/1/?title=what", HttpMethod.GET, professorHeader, new ParameterizedTypeReference<List<String>>() {
        });
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void listAllQuestionsShouldReturn200() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/list/1/", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getQuestionByIdWithoutIdShouldReturn400() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void getQuestionByIdWhenQuestionIdDoesNotExistsShouldReturn404() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/-1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void getQuestionByIdWhenQuestionExistsShouldReturn200() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteQuestionWhenIdExistsShouldReturn200() {
        long id = 1;
        BDDMockito.doNothing().when(questionRepository).deleteById(id);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/{id}", HttpMethod.GET, professorHeader, String.class, id);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    //
//    @Test
//    public void deleteQuestionWhenIdDoesNotExistsShouldReturn404() {
//
//    }

    @Test
    public void createQuestionWhenNameIsNullShouldReturn400() {
        Question question = questionRepository.findById(1L).get();
        question.setTitle(null);
        Assertions.assertThat(createQuestion(question).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createQuestionWhenEverythingIsRightShouldReturn201() {
        Question question = questionRepository.findById(1L).get();
        Assertions.assertThat(createQuestion(question).getStatusCodeValue()).isEqualTo(201);
    }

    private ResponseEntity<String> createQuestion(Question question) {
        BDDMockito.when(questionRepository.save(question)).thenReturn(question);
        return testRestTemplate.exchange("/v1/professor/course/question/", HttpMethod.POST, new HttpEntity<>(question, professorHeader.getHeaders()), String.class);
    }
}