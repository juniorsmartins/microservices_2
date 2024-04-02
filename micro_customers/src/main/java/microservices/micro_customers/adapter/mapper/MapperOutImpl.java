package microservices.micro_customers.adapter.mapper;

import microservices.micro_customers.adapter.out.entity.CustomerEntity;
import microservices.micro_customers.adapter.out.entity.value_objects.TelefoneVo;
import microservices.micro_customers.application.core.domain.Customer;
import microservices.micro_customers.application.core.domain.tipos.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MapperOutImpl implements MapperOut {

    @Override
    public CustomerEntity toCustomerEntity(Customer customer) {
        return Optional.ofNullable(customer)
            .map(this::entity)
            .orElseThrow();
    }

    private CustomerEntity entity(Customer customer) {
        var telefonesVo = this.toTelefoneVo(customer);

        return CustomerEntity.builder()
            .customerId(customer.getCustomerId())
            .nomeCompleto(customer.getNomeCompleto())
            .cpf(customer.getCpf().getCpf())
            .dataNascimento(customer.getDataNascimento().getDataNascimentoLocalDate())
            .statusCadastro(customer.getStatusCadastro())
            .email(customer.getEmail().getEmail())
            .telefones(telefonesVo)
            .cep(customer.getEndereco().getCep())
            .estado(customer.getEndereco().getEstado())
            .cidade(customer.getEndereco().getCidade())
            .bairro(customer.getEndereco().getBairro())
            .logradouro(customer.getEndereco().getLogradouro())
            .numero(customer.getEndereco().getNumero())
            .complemento(customer.getEndereco().getComplemento())
            .build();
    }

    private Set<TelefoneVo> toTelefoneVo(Customer customer) {
        if (customer.getTelefones() != null && !customer.getTelefones().isEmpty()) {
            return customer.getTelefones()
                .stream()
                .map(fone -> new TelefoneVo(fone.getTelefone(), fone.getTipo()))
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public Customer toCustomer(CustomerEntity entity) {
        return Optional.ofNullable(entity)
            .map(this::customer)
            .orElseThrow();
    }

    private Customer customer(CustomerEntity entity) {
        var telefones = this.toTelefone(entity);
        var endereco = this.toEndereco(entity);

        return Customer.builder()
            .customerId(entity.getCustomerId())
            .nomeCompleto(entity.getNomeCompleto())
            .cpf(new CadastroPessoaFisica(entity.getCpf()))
            .dataNascimento(new DataNascimento(entity.getDataNascimento()))
            .statusCadastro(entity.getStatusCadastro())
            .email(new CorreioEletronico(entity.getEmail()))
            .telefones(telefones)
            .endereco(endereco)
            .build();
    }

    private Set<Telefone> toTelefone(CustomerEntity entity) {
        if (entity.getTelefones() != null && !entity.getTelefones().isEmpty()) {
            return entity.getTelefones()
                .stream()
                .map(fone -> new Telefone(fone.getTelefone(), fone.getTipo()))
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Endereco toEndereco(CustomerEntity entity) {
        return Endereco.builder()
            .cep(entity.getCep())
            .estado(entity.getEstado())
            .cidade(entity.getCidade())
            .bairro(entity.getBairro())
            .logradouro(entity.getLogradouro())
            .numero(entity.getNumero())
            .complemento(entity.getComplemento())
            .build();
    }

}

