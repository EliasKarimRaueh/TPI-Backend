package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.CamionService;

import java.util.List;

@RestController
@RequestMapping("/api/camiones")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping
    public ResponseEntity<List<CamionDTO>> obtenerTodos() {
        List<CamionDTO> camiones = camionService.obtenerTodos();
        return ResponseEntity.ok(camiones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CamionDTO> obtenerPorId(@PathVariable Long id) {
        return camionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CamionDTO> crearCamion(@Valid @RequestBody CamionCreateDTO camionCreateDTO) {
        CamionDTO nuevoCamion = camionService.crearCamion(camionCreateDTO);
        return ResponseEntity.status(201).body(nuevoCamion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CamionDTO> actualizarCamion(
            @PathVariable Long id, 
            @Valid @RequestBody CamionUpdateDTO camionUpdateDTO) {
        CamionDTO camionActualizado = camionService.actualizarCamion(id, camionUpdateDTO);
        if (camionActualizado != null) {
            return ResponseEntity.ok(camionActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/camiones/disponibles
     * Obtener camiones disponibles con filtros opcionales
     * 
     * @param pesoMinimo capacidad mínima de peso requerida (opcional)
     * @param volumenMinimo capacidad mínima de volumen requerida (opcional)
     * @return Lista de camiones que cumplen los criterios
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<CamionDTO>> obtenerCamionesDisponibles(
            @RequestParam(required = false) Double pesoMinimo,
            @RequestParam(required = false) Double volumenMinimo) {

        List<CamionDTO> camionesDisponibles = camionService.buscarDisponibles(pesoMinimo, volumenMinimo);
        return ResponseEntity.ok(camionesDisponibles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable Long id) {
        camionService.eliminarCamion(id);
        return ResponseEntity.noContent().build();
    }
}