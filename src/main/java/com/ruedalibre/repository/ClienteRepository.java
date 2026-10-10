package com.ruedalibre.repository;

import com.ruedalibre.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCedula(String cedula);

    boolean existsByCedula(String cedula);

    boolean existsByCedulaAndIdNot(String cedula, Long id);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.cedula) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(c.correo) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Cliente> buscar(@Param("busqueda") String busqueda, Pageable pageable);
}
