package com.paschoalick.cleanarch.entrypoint.consumer.message;

// Uma vez que cadastra o cliente, vamos enviar o CPF
// Para o tópico do kafka e vai ter uma ter uma api mockada
// que vai ler o CPF, validar as informações e devolver
// num tópico do kafka essas informações

// Essa parte vai pegar do tópico do kafka e atualizar
// do nosso lado

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerMessage {

    private String id;

    private String name;

    private String zipCode;

    private String cpf;

    private Boolean isValidCpf;


}
