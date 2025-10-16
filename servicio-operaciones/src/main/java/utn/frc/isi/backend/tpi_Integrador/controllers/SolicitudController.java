package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.services.SolicitudService;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> obtenerTodas() {
        List<Solicitud> solicitudes = solicitudService.obtenerTodas();
        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtenerPorId(@PathVariable Long id) {
        return solicitudService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/solicitudes
     * Crear nueva solicitud de transporte con orquestación completa
     * RF#1 del documento de diseño - Endpoint principal del sistema
     * 
     * @param solicitudDTO datos de la solicitud (contenedor + cliente)
     * @return solicitud creada (201) o error (400)
     */
    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@Valid @RequestBody SolicitudCreateDTO solicitudDTO) {
        try {
            Solicitud nuevaSolicitud = solicitudService.crearNuevaSolicitud(solicitudDTO);
            return ResponseEntity.status(201).body(nuevaSolicitud);
        } catch (IllegalArgumentException e) {
            // Error de validación de datos de entrada
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Manejo básico de error si el cliente no se encuentra o hay errores de validación
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Solicitud> actualizarSolicitud(@PathVariable Long id, @RequestBody Solicitud solicitud) {
        Solicitud solicitudActualizada = solicitudService.actualizarSolicitud(id, solicitud);
        if (solicitudActualizada != null) {
            return ResponseEntity.ok(solicitudActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        solicitudService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}