package com.example.usuarioservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios") //  Adicione este mapeamento base na classe!
public class TesteConfigController {

    @Value("${mensagem:Mensagem padrão local}")
    private String mensagem;

    @GetMapping("/teste-config") //  Trata a rota final /teste-config
    public String getMensagem() {
        return this.mensagem;
    }
}
