package microservices.micro_customers.util;

import microservices.micro_customers.domain.enums.StatusCadastroEnum;
import microservices.micro_customers.domain.enums.TipoTelefoneEnum;
import microservices.micro_customers.entity.CustomerEntity;
import microservices.micro_customers.entity.value_objects.TelefoneVo;
import net.datafaker.Faker;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.Set;

// Padrão Object Mother
public final class FactoryObjectMother {

    private static FactoryObjectMother singletonFactory;

    private final Faker faker = new Faker();

    private FactoryObjectMother() { }

    // Padrão Singleton
    public static synchronized FactoryObjectMother singleton() {
        if (singletonFactory == null) {
            singletonFactory = new FactoryObjectMother();
        }
        return singletonFactory;
    }

    public static String gerarString(int numeroCaracteres) {
        return RandomStringUtils.randomAlphabetic(numeroCaracteres);
    }

    public TelefoneVo.TelefoneVoBuilder gerarTelefoneVoFixoBuilder() {
        return TelefoneVo.builder()
            .telefone(faker.phoneNumber().phoneNumber())
            .tipo(TipoTelefoneEnum.FIXO);
    }

    public TelefoneVo.TelefoneVoBuilder gerarTelefoneVoCelularBuilder() {
        return TelefoneVo.builder()
            .telefone(faker.phoneNumber().cellPhone())
            .tipo(TipoTelefoneEnum.CELULAR);
    }

    // Padrão Builder
    public CustomerEntity.CustomerEntityBuilder gerarCustomerEntityBuilder() {
        var telefone1 = gerarTelefoneVoFixoBuilder().build();
        var telefone2 = gerarTelefoneVoCelularBuilder().build();

        return CustomerEntity.builder()
            .nomeCompleto(faker.lorem().characters(20, 100))
            .cpf(faker.cpf().valid())
            .dataNascimento(LocalDate.of(2000, 10, 27))
            .statusCadastro(StatusCadastroEnum.INICIADO)
            .email(faker.internet().emailAddress())
            .telefones(Set.of(telefone1, telefone2))
                .cep(faker.address().zipCode())
                .estado(faker.address().state())
                .cidade(faker.address().city())
                .bairro(faker.lorem().characters(10, 20))
                .logradouro(faker.address().streetAddress())
                .numero(faker.address().buildingNumber())
                .complemento(faker.lorem().characters(30, 200));
    }

}
