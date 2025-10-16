package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaDTO;
import utn.frc.isi.backend.tpi_Integrador.services.TarifaService;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@CrossOrigin(origins = "*")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    /**
     * GET /api/tarifas/actual
     * Obtener la tarifa activa vigente del sistema
     * 
     * @return ResponseEntity con la tarifa activa o 404 si no existe
     */
    @GetMapping("/actual")
    public ResponseEntity<TarifaDTO> obtenerTarifaActiva() {
        return tarifaService.obtenerTarifaActivaDTO()
                .map(tarifaDTO -> ResponseEntity.ok(tarifaDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/tarifas
     * Listar todas las tarifas (históricas y activa)
     * 
     * @return ResponseEntity con lista de tarifas ordenadas por vigencia
     */
    @GetMapping
    public ResponseEntity<List<TarifaDTO>> obtenerTodas() {
        List<TarifaDTO> tarifas = tarifaService.obtenerTodasDTO();
        return ResponseEntity.ok(tarifas);
    }

    /**
     * GET /api/tarifas/{id}
     * Obtener tarifa por ID
     * 
     * @param id ID de la tarifa
     * @return ResponseEntity con la tarifa o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO> obtenerPorId(@PathVariable Long id) {
        return tarifaService.obtenerPorIdDTO(id)
                .map(tarifaDTO -> ResponseEntity.ok(tarifaDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/tarifas
     * Crear nueva tarifa
     * 
     * @param createDTO datos de la tarifa a crear
     * @return ResponseEntity con la tarifa creada (201) o error (400)
     */
    @PostMapping
    public ResponseEntity<TarifaDTO> crearTarifa(@Valid @RequestBody TarifaCreateDTO createDTO) {
        try {
            TarifaDTO nuevaTarifa = tarifaService.crearTarifaFromDTO(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarifa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/tarifas/{id}
     * Actualizar tarifa existente
     * 
     * @param id ID de la tarifa a actualizar
     * @param updateDTO datos actualizados
     * @return ResponseEntity con la tarifa actualizada o errores correspondientes
     */
    @PutMapping("/{id}")
    public ResponseEntity<TarifaDTO> actualizarTarifa(@PathVariable Long id, 
                                                      @Valid @RequestBody TarifaUpdateDTO updateDTO) {
        try {
            return tarifaService.actualizarTarifaFromDTO(id, updateDTO)
                    .map(tarifaActualizada -> ResponseEntity.ok(tarifaActualizada))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/tarifas/{id}
     * Eliminar tarifa por ID
     * NOTA: Solo se puede eliminar si no está activa
     * 
     * @param id ID de la tarifa a eliminar
     * @return ResponseEntity con 204 (No Content) o errores correspondientes
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id) {
        try {
            if (tarifaService.eliminarTarifa(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            // Tarifa activa no se puede eliminar
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/tarifas/existe-activa
     * Verificar si existe una tarifa activa (endpoint de utilidad)
     * 
     * @return ResponseEntity con boolean indicando si existe tarifa activa
     */
    @GetMapping("/existe-activa")
    public ResponseEntity<Boolean> existeTarifaActiva() {
        boolean existe = tarifaService.existeTarifaActiva();
        return ResponseEntity.ok(existe);
    }
}