package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorPendienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorUpdateDTO;
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
    public ResponseEntity<List<ContenedorDTO>> obtenerTodos() {
        List<ContenedorDTO> contenedores = contenedorService.obtenerTodos();
        return ResponseEntity.ok(contenedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenedorDTO> obtenerPorId(@PathVariable Long id) {
        return contenedorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContenedorDTO> crearContenedor(@Valid @RequestBody ContenedorCreateDTO contenedorCreateDTO) {
        ContenedorDTO nuevoContenedor = contenedorService.crearContenedor(contenedorCreateDTO);
        return ResponseEntity.status(201).body(nuevoContenedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenedorDTO> actualizarContenedor(
            @PathVariable Long id, 
            @Valid @RequestBody ContenedorUpdateDTO contenedorUpdateDTO) {
        ContenedorDTO contenedorActualizado = contenedorService.actualizarContenedor(id, contenedorUpdateDTO);
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
    
    /**
     * GET /api/contenedores/pendientes
     * RF#5: Consultar contenedores pendientes de asignación a transporte
     * Retorna la lista de contenedores que NO están en estado ENTREGADO
     * 
     * @return Lista de ContenedorPendienteDTO (200)
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<ContenedorPendienteDTO>> consultarContenedoresPendientes() {
        List<ContenedorPendienteDTO> pendientes = contenedorService.consultarPendientes();
        return ResponseEntity.ok(pendientes);
    }
}