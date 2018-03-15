package br.com.welson.examgenerator.endpoint.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/professor")
public class ProfessorEndpoint {

    @GetMapping
    public ResponseEntity<?> hi() {
        return new ResponseEntity<>("Hi", HttpStatus.OK);
    }
}
