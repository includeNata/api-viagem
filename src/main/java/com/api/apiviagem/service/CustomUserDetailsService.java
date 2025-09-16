package com.api.apiviagem.service;

import com.api.apiviagem.model.User;
import com.api.apiviagem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service // 👈 Anotação crucial para que o Spring reconheça esta classe como um Bean
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Este método é chamado pelo Spring Security quando ele precisa carregar
     * os dados de um usuário para realizar a autenticação.
     * @param username O nome de usuário (no nosso caso, o email) que está tentando se autenticar.
     * @return um objeto UserDetails contendo as informações do usuário.
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco de dados.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário no repositório pelo email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado com o email: " + username));

        // 2. Converte as roles (permissões) do seu usuário para o formato que o Spring Security entende
        //    (uma coleção de GrantedAuthority).
        //    Estou assumindo que sua entidade User tem um método getRoles() que retorna um Set<Role>
        //    e que a entidade Role tem um método getName() que retorna a string da role (ex: "ROLE_ADM").
        Set<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());

        String password = "";

        // 3. Retorna um objeto User do próprio Spring Security, que implementa UserDetails.
        //    Este objeto contém o email, a senha (hash) e as permissões do usuário.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                password, // O Spring Security precisa da senha para o fluxo padrão de login
                authorities
        );
    }
}
