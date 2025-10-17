package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.AsignacionCamionDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;
import utn.frc.isi.backend.tpi_Integrador.services.TramoService;

import java.util.List;

@RestController
@RequestMapping("/api/tramos")
public class TramoController {

    private final TramoService tramoService;

    public TramoController(TramoService tramoService) {
        this.tramoService = tramoService;
    }

    @GetMapping
    public ResponseEntity<List<Tramo>> obtenerTodos() {
        List<Tramo> tramos = tramoService.obtenerTodos();
        return ResponseEntity.ok(tramos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable Long id) {
        return tramoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tramo> crearTramo(@RequestBody Tramo tramo) {
        Tramo nuevoTramo = tramoService.crearTramo(tramo);
        return ResponseEntity.status(201).body(nuevoTramo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tramo> actualizarTramo(@PathVariable Long id, @RequestBody Tramo tramo) {
        Tramo tramoActualizado = tramoService.actualizarTramo(id, tramo);
        if (tramoActualizado != null) {
            return ResponseEntity.ok(tramoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTramo(@PathVariable Long id) {
        tramoService.eliminarTramo(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * POST /api/tramos/{id}/asignar-camion
     * RF#6: Asignar un camión a un tramo específico
     * Valida disponibilidad y capacidad del camión antes de la asignación
     * Cambia el estado del tramo a "ASIGNADO" y marca el camión como no disponible
     * 
     * @param id ID del tramo
     * @param asignacionDTO DTO con el ID del camión a asignar
     * @return Tramo actualizado (200) o error (400, 404)
     */
    @PostMapping("/{id}/asignar-camion")
    public ResponseEntity<Tramo> asignarCamion(@PathVariable Long id, @Valid @RequestBody AsignacionCamionDTO asignacionDTO) {
        try {
            Tramo tramoActualizado = tramoService.asignarCamion(id, asignacionDTO);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request con el mensaje de error
            return ResponseEntity.badRequest().build();
        }
    }
}