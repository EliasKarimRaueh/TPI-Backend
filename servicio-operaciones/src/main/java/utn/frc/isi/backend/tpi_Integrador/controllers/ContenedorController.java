package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.services.ContenedorService;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    @GetMapping
    public ResponseEntity<List<Contenedor>> obtenerTodos() {
        List<Contenedor> contenedores = contenedorService.obtenerTodos();
        return ResponseEntity.ok(contenedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerPorId(@PathVariable Long id) {
        return contenedorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Contenedor> crearContenedor(@RequestBody Contenedor contenedor) {
        Contenedor nuevoContenedor = contenedorService.crearContenedor(contenedor);
        return ResponseEntity.status(201).body(nuevoContenedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> actualizarContenedor(@PathVariable Long id, @RequestBody Contenedor contenedor) {
        Contenedor contenedorActualizado = contenedorService.actualizarContenedor(id, contenedor);
        if (contenedorActualizado != null) {
            return ResponseEntity.ok(contenedorActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContenedor(@PathVariable Long id) {
        contenedorService.eliminarContenedor(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/contenedores/{id}/estado
     * RF#2: Consultar estado de contenedor para seguimiento
     * Permite al cliente consultar el estado y ubicación actual de su contenedor
     * 
     * @param id ID del contenedor
     * @return ContenedorEstadoDTO con información de estado (200) o Not Found (404)
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<ContenedorEstadoDTO> consultarEstadoContenedor(@PathVariable Long id) {
        return contenedorService.consultarEstado(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}