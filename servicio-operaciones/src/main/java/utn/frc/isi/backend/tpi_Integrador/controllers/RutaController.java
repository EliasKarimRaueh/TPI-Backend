package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.RutaService;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping
    public ResponseEntity<List<RutaDTO>> obtenerTodas() {
        List<RutaDTO> rutas = rutaService.obtenerTodas();
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaDTO> obtenerPorId(@PathVariable Long id) {
        return rutaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Nota: Las rutas se crean mediante POST /api/solicitudes/{solicitudId}/asignar-ruta (RF#4)
     * No hay endpoint POST /api/rutas directo
     */

    @PutMapping("/{id}")
    public ResponseEntity<RutaDTO> actualizarRuta(@PathVariable Long id, @Valid @RequestBody RutaUpdateDTO ruta) {
        RutaDTO rutaActualizada = rutaService.actualizarRuta(id, ruta);
        if (rutaActualizada != null) {
            return ResponseEntity.ok(rutaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}