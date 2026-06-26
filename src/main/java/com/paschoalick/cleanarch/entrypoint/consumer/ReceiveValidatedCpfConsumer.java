package com.paschoalick.cleanarch.entrypoint.consumer;

import com.paschoalick.cleanarch.core.usecase.UpdateCustomerUseCase;
import com.paschoalick.cleanarch.entrypoint.consumer.mapper.CustomerMessageMapper;
import com.paschoalick.cleanarch.entrypoint.consumer.message.CustomerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveValidatedCpfConsumer {

    @Autowired
    private UpdateCustomerUseCase updateCustomerUseCase;

    @Autowired
    private CustomerMessageMapper customerMessageMapper;

    // Vai ler de outro tópico que já tiver validado o CPF
    @KafkaListener(topics = "tp-cfp-validated", groupId = "paschoalick")
    public void receive(CustomerMessage customerMessage) {

        // pega da fila do kafka, conveter a mensagem para  customer
        // e usar o update
        var customer = customerMessageMapper.toCustomer(customerMessage);
        updateCustomerUseCase.update(customer, customerMessage.getZipCode());
    }


}
