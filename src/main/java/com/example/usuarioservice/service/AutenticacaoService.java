package com.example.usuarioservice.service;

import com.example.usuarioservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega os dados do usuário no banco para o processo de autenticação do Spring Security.
 */
@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Procura o usuário pelo e-mail ou matrícula e lança exceção caso não exista.
        return usuarioRepository.findByEmailOrMatricula(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não localizado: " + login));
    }
}
