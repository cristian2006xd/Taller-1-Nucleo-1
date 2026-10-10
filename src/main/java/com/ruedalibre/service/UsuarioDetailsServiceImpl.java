package com.ruedalibre.service;

import com.ruedalibre.model.Usuario;
import com.ruedalibre.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + nombreUsuario);
        }

        return new User(
                usuario.getNombreUsuario(),
                usuario.getContrasena(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}
