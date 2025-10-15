package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody Solicitud solicitud) {
        Solicitud nuevaSolicitud = solicitudService.crearSolicitud(solicitud);
        return ResponseEntity.status(201).body(nuevaSolicitud);
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