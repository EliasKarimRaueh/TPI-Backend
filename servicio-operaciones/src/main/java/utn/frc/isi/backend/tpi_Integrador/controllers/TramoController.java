package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.AsignacionCamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TramoDTO;
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
    public ResponseEntity<List<TramoDTO>> obtenerTodos() {
        List<TramoDTO> tramos = tramoService.obtenerTodos();
        return ResponseEntity.ok(tramos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramoDTO> obtenerPorId(@PathVariable Long id) {
        return tramoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/tramos/{id}/asignar-camion
     * RF#6: Asignar un camión a un tramo específico
     * Valida disponibilidad y capacidad del camión antes de la asignación
     * Cambia el estado del tramo a "ASIGNADO" y marca el camión como no disponible
     * 
     * @param id ID del tramo
     * @param asignacionDTO DTO con el ID del camión a asignar
     * @return TramoDTO actualizado (200) o error (400, 404)
     */
    @PostMapping("/{id}/asignar-camion")
    public ResponseEntity<TramoDTO> asignarCamion(@PathVariable Long id, @Valid @RequestBody AsignacionCamionDTO asignacionDTO) {
        try {
            TramoDTO tramoActualizado = tramoService.asignarCamion(id, asignacionDTO);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request con el mensaje de error
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * POST /api/tramos/{id}/iniciar
     * RF#8: Iniciar un tramo de transporte
     * El transportista marca el inicio del viaje
     * Actualiza el estado del tramo a "INICIADO" y el contenedor a "EN_VIAJE"
     * 
     * @param id ID del tramo a iniciar
     * @return TramoDTO actualizado (200) o error (400, 404)
     */
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<TramoDTO> iniciarTramo(@PathVariable Long id) {
        try {
            TramoDTO tramoIniciado = tramoService.iniciarTramo(id);
            return ResponseEntity.ok(tramoIniciado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request en caso de validación fallida
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * POST /api/tramos/{id}/finalizar
     * RF#8: Finalizar un tramo de transporte
     * El transportista marca el fin del viaje
     * Calcula el costo real del tramo usando datos de servicio-flota
     * Actualiza el estado del tramo a "FINALIZADO"
     * Si es el último tramo, marca contenedor como "ENTREGADO" y solicitud como "ENTREGADA"
     * Libera el camión para nuevas asignaciones
     * 
     * @param id ID del tramo a finalizar
     * @return TramoDTO actualizado con costo real (200) o error (400, 404)
     */
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<TramoDTO> finalizarTramo(@PathVariable Long id) {
        try {
            TramoDTO tramoFinalizado = tramoService.finalizarTramo(id);
            return ResponseEntity.ok(tramoFinalizado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request en caso de validación fallida
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * GET /api/tramos/transportistas/{camionId}/tramos
     * RF#7: Obtener tramos asignados a un transportista que no están finalizados
     * Permite al transportista ver sus tramos pendientes de ejecución
     * 
     * @param camionId ID del camión asignado al transportista
     * @return Lista de TramoDTO con los tramos asignados (200 OK)
     */
    @GetMapping("/transportistas/{camionId}/tramos")
    public ResponseEntity<List<TramoDTO>> obtenerTramosParaTransportista(@PathVariable Long camionId) {
        List<TramoDTO> tramosAsignados = tramoService.obtenerTramosAsignadosTransportista(camionId);
        // Devuelve 200 OK con lista vacía si no hay tramos, o con los tramos encontrados
        return ResponseEntity.ok(tramosAsignados);
    }
}