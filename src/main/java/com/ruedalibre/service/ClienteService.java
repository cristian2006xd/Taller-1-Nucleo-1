package com.ruedalibre.service;

import com.ruedalibre.exception.RecursoNoEncontradoException;
import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Cliente;
import com.ruedalibre.repository.ClienteRepository;
import com.ruedalibre.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VentaRepository ventaRepository;

    public Page<Cliente> buscar(String busqueda, Pageable pageable) {
        if (busqueda != null && !busqueda.isBlank()) {
            return clienteRepository.buscar(busqueda, pageable);
        }
        return clienteRepository.findAll(pageable);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    public Cliente guardar(Cliente cliente) {
        if (cliente.getId() == null) {
            if (clienteRepository.existsByCedula(cliente.getCedula())) {
                throw new ReglaNegocioException("Ya existe un cliente con la cédula: " + cliente.getCedula());
            }
            log.info("Creando cliente: {}", cliente.getCedula());
        } else {
            if (clienteRepository.existsByCedulaAndIdNot(cliente.getCedula(), cliente.getId())) {
                throw new ReglaNegocioException("Ya existe un cliente con la cédula: " + cliente.getCedula());
            }
            log.info("Editando cliente id={}: {}", cliente.getId(), cliente.getCedula());
        }
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        if (ventaRepository.existsByCliente(cliente)) {
            throw new ReglaNegocioException("No se puede eliminar el cliente porque tiene ventas registradas.");
        }
        log.info("Eliminando cliente id={}: {}", id, cliente.getCedula());
        clienteRepository.deleteById(id);
    }
}
