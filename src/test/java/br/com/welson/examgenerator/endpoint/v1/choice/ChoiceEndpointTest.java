package br.com.welson.examgenerator.endpoint.v1.choice;

import br.com.welson.examgenerator.endpoint.v1.ProfessorEndpointTest;
import br.com.welson.examgenerator.endpoint.v1.question.QuestionEndpointTest;
import br.com.welson.examgenerator.persistence.model.Choice;
import br.com.welson.examgenerator.persistence.model.Question;
import br.com.welson.examgenerator.persistence.repository.ChoiceRepository;
import br.com.welson.examgenerator.persistence.repository.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChoiceEndpointTest {

    @MockBean
    private ChoiceRepository choiceRepository;
    @MockBean
    private QuestionRepository questionRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Choice choiceCorrectAnswerFalse = mockChoice(1L, false);
    private Choice choiceCorrectAnswerTrue = mockChoice(2L, true);

    private static Choice mockChoice(Long id, boolean isCorrectChoice) {
        return Choice.ChoiceBuilder.newChoice()
                .id(1L)
                .title("Is a room")
                .correctAnswer(isCorrectChoice)
                .question(QuestionEndpointTest.mockQuestion())
                .professor(ProfessorEndpointTest.mockProfessor())
                .build();
    }

    @Before
    public void configureProfessorHeader() {
        String body = "{\"username\":\"welson\",\"password\":\"welson\"}";
        HttpHeaders headers = testRestTemplate.postForEntity("/login", body, String.class).getHeaders();
        this.professorHeader = new HttpEntity<>(headers);
    }

    @Before
    public void cofigureWrongHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "123456");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Before
    public void setUp() throws Exception {
        BDDMockito.when(choiceRepository.findById(choiceCorrectAnswerFalse.getId())).thenReturn(Optional.of(choiceCorrectAnswerFalse));
        BDDMockito.when(choiceRepository.findById(choiceCorrectAnswerTrue.getId())).thenReturn(Optional.of(choiceCorrectAnswerTrue));
        BDDMockito.when(choiceRepository.findAllByQuestionId(choiceCorrectAnswerFalse.getQuestion().getId())).thenReturn(Arrays.asList(choiceCorrectAnswerFalse, choiceCorrectAnswerTrue));
        BDDMockito.when(questionRepository.findById(choiceCorrectAnswerFalse.getQuestion().getId())).thenReturn(Optional.of(choiceCorrectAnswerFalse.getQuestion()));
        BDDMockito.when(questionRepository.findById(choiceCorrectAnswerTrue.getQuestion().getId())).thenReturn(Optional.of(choiceCorrectAnswerTrue.getQuestion()));
    }

    @Test
    public void getChoiceByIdWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/1", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listChocesByQuestionIdWhenTokenIsWrongShouldReturn403() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/list/1", HttpMethod.GET, wrongHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void listChoicesByQuestionIdWhenQuestionIdDoesNotExistsShouldReturnEmptyList() {
        ResponseEntity<List<Choice>> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/list/2", HttpMethod.GET, professorHeader, new ParameterizedTypeReference<List<Choice>>() {
        });
        Assertions.assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void listChoicesByQuestionIdWhenQuestionIdExistsShouldReturnListWithChoices() {
        ResponseEntity<List<Choice>> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/list/1", HttpMethod.GET, professorHeader, new ParameterizedTypeReference<List<Choice>>() {
        });
        Assertions.assertThat(exchange.getBody()).isNotEmpty();
    }

    @Test
    public void getChoiceByIdWithoutIdShouldReturn400() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void getChoiceByIdWhenChoiceIdDoesNotExistsShouldReturn404() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/-1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void getChoiceByIdWhenChoiceIdExistsShouldReturn200() {
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/1", HttpMethod.GET, professorHeader, String.class);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteChoiceByIdWhenIdExistsReturn200() {
        BDDMockito.doNothing().when(choiceRepository).delete(choiceCorrectAnswerTrue);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/{id}", HttpMethod.DELETE, professorHeader, String.class, 1L);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deleteChoiceByIdWhenIdDoesNotExistsReturn404() {
        BDDMockito.doNothing().when(choiceRepository).delete(choiceCorrectAnswerTrue);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/v1/professor/course/question/choice/{id}", HttpMethod.DELETE, professorHeader, String.class, -1L);
        Assertions.assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void createChoiceWhenTitleIsNullShouldReturn400() {
        Choice choice = choiceRepository.findById(1L).get();
        choice.setTitle(null);
        Assertions.assertThat(createChoice(choice).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void createChoiceWhenQuestionDoesNotExistsShouldReturn404() {
        Choice choice = choiceRepository.findById(1L).get();
        choice.setQuestion(new Question());
        Assertions.assertThat(createChoice(choice).getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void createChoiceWhenEverythingIsRightShouldReturn201() {
        Choice choice = choiceRepository.findById(1L).get();
        choice.setId(null);
        Assertions.assertThat(createChoice(choice).getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void createChoiceWithCorrectAnswerTrueShouldTriggerUpdateChoicesAndCreate() {
        Choice choice = choiceRepository.findById(1L).get();
        choice.setId(null);
        createChoice(choice).getBody();
        BDDMockito.verify(choiceRepository, Mockito.times(1)).save(choice);
        BDDMockito.verify(choiceRepository, Mockito.times(1)).updateAllOtherChoicesCorrectAnswerToFalse(choice, choice.getQuestion());
    }

    private ResponseEntity<String> createChoice(Choice choice) {
        BDDMockito.when(choiceRepository.save(choice)).thenReturn(choice);
        return testRestTemplate.exchange("/v1/professor/course/question/choice/", HttpMethod.POST, new HttpEntity<>(choice, professorHeader.getHeaders()), String.class);
    }
}