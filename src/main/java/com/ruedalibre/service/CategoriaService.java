package com.ruedalibre.service;

import com.ruedalibre.exception.RecursoNoEncontradoException;
import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Categoria;
import com.ruedalibre.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    public Categoria guardar(Categoria categoria) {
        if (categoria.getId() == null) {
            if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
                throw new ReglaNegocioException("Ya existe una categoría con el nombre: " + categoria.getNombre());
            }
            log.info("Creando categoría: {}", categoria.getNombre());
        } else {
            if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(categoria.getNombre(), categoria.getId())) {
                throw new ReglaNegocioException("Ya existe una categoría con el nombre: " + categoria.getNombre());
            }
            log.info("Editando categoría id={}: {}", categoria.getId(), categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos asociados.");
        }
        log.info("Eliminando categoría id={}: {}", id, categoria.getNombre());
        categoriaRepository.deleteById(id);
    }
}
