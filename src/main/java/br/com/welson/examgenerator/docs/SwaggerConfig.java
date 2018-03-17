package br.com.welson.examgenerator.docs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.welson.examgenerator.endpoint.v1"))
                .build()
                .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Exam generator by DevDojo")
                .description("Software to generate exams based on questions")
                .version("1.0")
                .contact(new Contact("Welson", "https://github.com/welsonlimawlsn", "welsonlimawlsn@gmail.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apche.org/licenses/LICENSE-2.0")
                .build();
    }
}
