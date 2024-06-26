package microservices.micro_empresas.adapter.in.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import microservices.micro_empresas.adapter.in.controller.dto.response.EmpresaCreateDtoResponse;
import microservices.micro_empresas.adapter.out.entity.EmpresaEntity;
import microservices.micro_empresas.adapter.out.repository.EmpresaRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import util.AbstractTestcontainersTest;
import util.FactoryObjectMother;
import util.TestConfig;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisplayName("Integration - EmpresaController")
class EmpresaControllerIntegrationTest extends AbstractTestcontainersTest {

    static final String BASE_PATH = "/api/v1/empresas";

    static RequestSpecification requestSpecification;

    static ObjectMapper objectMapper;

    final FactoryObjectMother factory = FactoryObjectMother.singleton();

    @LocalServerPort // Esta anotação injeta a porta selecionada pelo Spring Boot
    int port;

    @Autowired
    EmpresaRepository empresaRepository;

    EmpresaEntity entity1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper(); // Precisei mudar o import do ObjectMapper
//        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // Usar somente nos testes para manter a segurança da API - Isso é usado quanto temos Hateoas
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());

        requestSpecification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_BABYSTEPS)
            .setBasePath(BASE_PATH)
            .setPort(port)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        entity1 = factory.gerarEmpresaEntityBuilder().build();
        this.empresaRepository.save(entity1);
    }

    @AfterEach
    void tearDown() {
        this.empresaRepository.deleteAll();
    }

    @Nested
    @DisplayName("Post")
    class PostCreate {

        @Test
        @DisplayName("dados válidos")
        void dadoEmpresaValida_quandoCreate_entaoRetornarDadosPersistidos() throws IOException {

            var dtoIn = factory.gerarEmpresaCreateDtoRequestBuilder().build();

            var response = RestAssured
                .given().spec(requestSpecification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .body(dtoIn)
                .when()
                    .post()
                .then()
                    .log().all()
                    .statusCode(201)
                .extract()
                    .body()
                        .asString();

            var dtoOut = objectMapper.readValue(response, EmpresaCreateDtoResponse.class);
            var persistido = empresaRepository.findById(dtoOut.getEmpresaId()).orElseThrow();

            Assertions.assertEquals(dtoOut.getEmpresaId(), persistido.getEmpresaId());
            Assertions.assertEquals(dtoOut.getNome(), persistido.getNome());
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTest {

        @Test
        @DisplayName("id válido")
        void dadoIdValido_quandoDelete_entaoRetornarHttp204NoContent() {

            RestAssured
                .given().spec(requestSpecification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .delete("/" + entity1.getEmpresaId())
                .then()
                    .log().all()
                    .statusCode(204);

            var persistido = empresaRepository.findById(entity1.getEmpresaId());

            Assertions.assertTrue(persistido.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get List")
    class GetList {

        @Test
        @DisplayName("all")
        void dadoRequisicaoValida_quandoList_entaoRetornarListaComDoisItens() {

            var empresaEntidade2 = factory.gerarEmpresaEntityBuilder().build();
            empresaRepository.save(empresaEntidade2);

            RestAssured
                .given().spec(requestSpecification)
                    .contentType(TestConfig.CONTENT_TYPE_JSON)
                .when()
                    .get()
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("", Matchers.hasSize(2));
        }
    }

}

