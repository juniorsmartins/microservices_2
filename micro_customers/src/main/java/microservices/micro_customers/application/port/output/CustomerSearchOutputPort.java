package microservices.micro_customers.application.port.output;

import microservices.micro_customers.adapter.dto.response.CustomerSearchDtoResponse;
import microservices.micro_customers.adapter.in.filters.CustomerFilter;
import microservices.micro_customers.application.core.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerSearchOutputPort {

    Page<CustomerSearchDtoResponse> search(CustomerFilter customerFilter, Pageable pagination);

}

