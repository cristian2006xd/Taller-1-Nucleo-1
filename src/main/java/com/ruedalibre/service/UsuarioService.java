package com.ruedalibre.service;

import com.ruedalibre.exception.RecursoNoEncontradoException;
import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.enums.RolUsuario;
import com.ruedalibre.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<Usuario> buscar(String busqueda, Pageable pageable) {
        if (busqueda != null && !busqueda.isBlank()) {
            return usuarioRepository.buscar(busqueda, pageable);
        }
        return usuarioRepository.findAll(pageable);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    public Usuario guardar(Usuario usuario, String nuevaContrasena) {
        if (usuario.getId() == null) {
            if (usuarioRepository.existsByNombreUsuario(usuario.getNombreUsuario())) {
                throw new ReglaNegocioException("Ya existe un usuario con el nombre: " + usuario.getNombreUsuario());
            }
            if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
                throw new ReglaNegocioException("Ya existe un usuario con el correo: " + usuario.getCorreo());
            }
            if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
                throw new ReglaNegocioException("La contraseña es obligatoria para nuevos usuarios.");
            }
            usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
            log.info("Creando usuario: {}", usuario.getNombreUsuario());
        } else {
            if (usuarioRepository.existsByNombreUsuarioAndIdNot(usuario.getNombreUsuario(), usuario.getId())) {
                throw new ReglaNegocioException("Ya existe un usuario con el nombre: " + usuario.getNombreUsuario());
            }
            if (usuarioRepository.existsByCorreoAndIdNot(usuario.getCorreo(), usuario.getId())) {
                throw new ReglaNegocioException("Ya existe un usuario con el correo: " + usuario.getCorreo());
            }
            Usuario existente = buscarPorId(usuario.getId());
            if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
                usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
            } else {
                usuario.setContrasena(existente.getContrasena());
            }
            log.info("Editando usuario id={}: {}", usuario.getId(), usuario.getNombreUsuario());
        }
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getRol() == RolUsuario.ADMIN) {
            long adminsActivos = usuarioRepository.countByRolAndActivoTrue(RolUsuario.ADMIN);
            if (adminsActivos <= 1) {
                throw new ReglaNegocioException("No se puede eliminar al último administrador activo.");
            }
        }
        log.info("Eliminando usuario id={}: {}", id, usuario.getNombreUsuario());
        usuarioRepository.deleteById(id);
    }

    public void toggleActivo(Long id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getActivo() && usuario.getRol() == RolUsuario.ADMIN) {
            long adminsActivos = usuarioRepository.countByRolAndActivoTrue(RolUsuario.ADMIN);
            if (adminsActivos <= 1) {
                throw new ReglaNegocioException("No se puede desactivar al último administrador activo.");
            }
        }
        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);
        log.info("Usuario id={} {} activo={}", id, usuario.getNombreUsuario(), usuario.getActivo());
    }

    public boolean existeAlgunAdmin() {
        return usuarioRepository.existsByRol(RolUsuario.ADMIN);
    }
}
