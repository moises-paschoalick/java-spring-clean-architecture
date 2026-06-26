package com.paschoalick.cleanarch.entrypoint.controller;

import com.paschoalick.cleanarch.core.usecase.DeleteCustomerByIdUseCase;
import com.paschoalick.cleanarch.core.usecase.FindCustomerByIdUseCase;
import com.paschoalick.cleanarch.core.usecase.InsertCustomerUseCase;
import com.paschoalick.cleanarch.core.usecase.UpdateCustomerUseCase;
import com.paschoalick.cleanarch.entrypoint.controller.mapper.CustomerMapper;
import com.paschoalick.cleanarch.entrypoint.controller.request.CustomerRequest;
import com.paschoalick.cleanarch.entrypoint.controller.response.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {


    @Autowired
    private InsertCustomerUseCase insertCustomerUseCase;

    @Autowired
    private FindCustomerByIdUseCase findCustomerByIdUseCase;

    @Autowired
    private UpdateCustomerUseCase updateCustomerUseCase;

    @Autowired
    private DeleteCustomerByIdUseCase deleteCustomerByIdUseCase;

    @Autowired
    private CustomerMapper customerMapper;

    @PostMapping
    public ResponseEntity<Void> insert(@Valid @RequestBody CustomerRequest customerRequest) {
        var customer = customerMapper.toCustomer(customerRequest);
        insertCustomerUseCase.insert(customer, customerRequest.getZipCode());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable final String id) {
            var customer = findCustomerByIdUseCase.find(id);
            var customerResponse = customerMapper.toCustomerRespone(customer);
            return ResponseEntity.ok().body(customerResponse);
    }

    /**
     * Uso do final como parâmetro
     * Como String é imutável e o parâmetro é uma cópia da referência, esse final não muda nada no comportamento externo — é puramente uma restrição local de estilo. Serve para:
     *
     * Sinalizar intenção: deixar claro que id representa o valor recebido e não deve ser modificado ao longo do método.
     * Prevenir bugs sutis: evita que alguém reatribua o parâmetro por engano em vez de criar uma variável nova.
     *
     * Na prática é opcional e divide opiniões. Muita gente acha verboso e omite (o Spring não exige). Outros adotam como convenção em todo parâmetro para reforçar imutabilidade. Diferente do campo final, aqui não há ganho de thread-safety nem garantia estrutural relevante
     */

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final String id, @Valid @RequestBody CustomerRequest customerRequest) {
        var customer = customerMapper.toCustomer(customerRequest);
        customer.setId(id);
        updateCustomerUseCase.update(customer, customerRequest.getZipCode());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        deleteCustomerByIdUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
