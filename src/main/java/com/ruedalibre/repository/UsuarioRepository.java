package com.ruedalibre.repository;

import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.enums.RolUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuarioAndIdNot(String nombreUsuario, Long id);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    long countByRolAndActivoTrue(RolUsuario rol);

    boolean existsByRol(RolUsuario rol);

    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Usuario> buscar(@Param("busqueda") String busqueda, Pageable pageable);
}
