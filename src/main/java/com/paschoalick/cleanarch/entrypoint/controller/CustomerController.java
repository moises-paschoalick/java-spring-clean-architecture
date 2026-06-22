package com.paschoalick.cleanarch.entrypoint.controller;

import com.paschoalick.cleanarch.core.dataprovider.InsertCustomer;
import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.core.usecase.FindCustomerByIdUseCase;
import com.paschoalick.cleanarch.core.usecase.InsertCustomerUseCase;
import com.paschoalick.cleanarch.entrypoint.controller.mapper.CustomerMapper;
import com.paschoalick.cleanarch.entrypoint.controller.request.CustomerRequest;
import com.paschoalick.cleanarch.entrypoint.controller.respone.CustomerResponse;
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

}
