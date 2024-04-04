package microservices.micro_customers.adapter.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import microservices.micro_customers.adapter.dto.EnderecoDto;
import microservices.micro_customers.adapter.dto.TelefoneDto;
import microservices.micro_customers.application.core.domain.enums.StatusCadastroEnum;

import java.time.OffsetDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerSearchDtoResponse(

        Long customerId,

        String nomeCompleto,

        String cpf,

        String dataNascimento,

        StatusCadastroEnum statusCadastro,

        String email,

        Set<TelefoneDto> telefones,

        Set<EnderecoDto> enderecos,

        OffsetDateTime createdAt,

        String createdBy,

        OffsetDateTime updatedAt,

        String updatedBy

) { }

