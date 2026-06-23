package com.paschoalick.cleanarch.core.usecase.impl;

import com.paschoalick.cleanarch.core.dataprovider.FindAddressByZipCode;
import com.paschoalick.cleanarch.core.dataprovider.UpdateCustomer;
import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.core.usecase.FindCustomerByIdUseCase;
import com.paschoalick.cleanarch.core.usecase.UpdateCustomerUseCase;

/**
 * private esconde a dependência de fora da classe (encapsulamento) — ninguém acessa ou troca o campo diretamente.private esconde a dependência de fora da classe (encapsulamento) — ninguém acessa ou troca o campo diretamente.
 * final garante que o campo só é atribuído uma vez, no construtor, e nunca mais muda. Isso te dá:
 *
 * Imutabilidade da referência: a dependência injetada não pode ser sobrescrita acidentalmente depois.
 * Garantia de inicialização: o compilador obriga você a atribuir o campo no construtor (ou na declaração), evitando dependências em estado nulo por esquecimento.
 * Thread-safety: campos final têm garantias de visibilidade no modelo de memória da JVM após a construção do objeto.
 *
 */

public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    // Não pode usar o AutoWired pois está no core e precisa ser livre de framework
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;

    // FindAddressByZipCode está em: Core>dataprovider>
    private final FindAddressByZipCode findAddressByZipCode;

    private final UpdateCustomer updateCustomer;

    public UpdateCustomerUseCaseImpl(
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            FindAddressByZipCode findAddressByZipCode,
            UpdateCustomer updateCustomer
            ) {
        this.findCustomerByIdUseCase = findCustomerByIdUseCase;
        this.findAddressByZipCode = findAddressByZipCode;
        this.updateCustomer = updateCustomer;

    }

    @Override
    public void update(Customer customer, String zipCode) {
        // Verificar se o id do usuário existe na base de dados
        findCustomerByIdUseCase.find(customer.getId());
        var address = findAddressByZipCode.find(zipCode);
        customer.setAddress(address);
        updateCustomer.update(customer);
    }
}
