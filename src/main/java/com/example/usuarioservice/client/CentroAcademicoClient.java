package com.example.usuarioservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Define o cliente HTTP declarativo que localiza o serviço acadêmico pelo Eureka.
@FeignClient(name = "espacos-academicos-service")
public interface CentroAcademicoClient {

    @GetMapping("/api/centros-academicos/{id}")
    CentroAcademicoExternoDTO buscarPorId(@PathVariable("id") Long id);
}
