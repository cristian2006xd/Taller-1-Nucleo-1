package com.ruedalibre.config;

import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.enums.RolUsuario;
import com.ruedalibre.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInicialConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicialConfig.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!usuarioRepository.existsByRol(RolUsuario.ADMIN)) {
            String username = System.getenv("ADMIN_USERNAME");
            String password = System.getenv("ADMIN_PASSWORD");

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                log.warn("No existe ningún ADMIN y las variables ADMIN_USERNAME / ADMIN_PASSWORD no están definidas. Crea un usuario ADMIN manualmente.");
                return;
            }

            Usuario admin = new Usuario();
            admin.setNombreCompleto("Administrador");
            admin.setNombreUsuario(username);
            admin.setCorreo(username + "@ruedalibre.com");
            admin.setContrasena(passwordEncoder.encode(password));
            admin.setRol(RolUsuario.ADMIN);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            log.info("Usuario ADMIN inicial creado: {}", username);
        }
    }
}
