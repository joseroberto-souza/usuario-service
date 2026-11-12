package com.example.usuarioservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expõe endpoints REST para validar o carregamento de configurações remotas.
 * Esta classe confirma que o microsserviço lê as propriedades do Config Server.
 */
@RestController // Define esta classe como um controlador REST que retorna dados diretamente no corpo da resposta.
@RequestMapping("/api/usuarios") // Define o prefixo de URL base para todos os endpoints desta classe.
public class TesteConfigController {

    // Injeta o valor da propriedade 'mensagem' do Config Server ou usa o texto padrão local se ela falhar.
    @Value("${mensagem:Mensagem padrão local}")
    private String mensagem;

    /**
     * Retorna a mensagem de teste carregada da configuração externa.
     *
     * @return Texto da propriedade configurada no servidor remoto ou o valor padrão.
     */
    @GetMapping("/teste-config") // Mapeia requisições HTTP GET para a rota final /api/usuarios/teste-config.
    public String getMensagem() {
        // Devolve o conteúdo da variável mensagem para o cliente HTTP.
        return this.mensagem;
    }
}
