package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaTentativaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.services.RutaService;
import utn.frc.isi.backend.tpi_Integrador.services.SolicitudService;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final RutaService rutaService;

    public SolicitudController(SolicitudService solicitudService, RutaService rutaService) {
        this.solicitudService = solicitudService;
        this.rutaService = rutaService;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudDTO>> obtenerTodas() {
        List<SolicitudDTO> solicitudes = solicitudService.obtenerTodas();
        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtenerPorId(@PathVariable Long id) {
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
    public ResponseEntity<SolicitudDTO> crearSolicitud(@Valid @RequestBody SolicitudCreateDTO solicitudDTO) {
        try {
            SolicitudDTO nuevaSolicitud = solicitudService.crearNuevaSolicitud(solicitudDTO);
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
    public ResponseEntity<SolicitudDTO> actualizarSolicitud(@PathVariable Long id, @Valid @RequestBody SolicitudUpdateDTO solicitud) {
        SolicitudDTO solicitudActualizada = solicitudService.actualizarSolicitud(id, solicitud);
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
    
    /**
     * GET /api/solicitudes/{id}/estado
     * RF#2: Consultar estado completo del transporte
     * Permite al cliente consultar el estado detallado de su solicitud,
     * incluyendo ubicación del contenedor, progreso y ETA
     * 
     * @param id ID de la solicitud
     * @return SolicitudEstadoDTO con información completa (200) o Not Found (404)
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<SolicitudEstadoDTO> consultarEstadoSolicitud(@PathVariable Long id) {
        return solicitudService.consultarEstadoSolicitud(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/solicitudes/{solicitudId}/rutas/tentativas
     * RF#3: Consultar rutas tentativas con cálculos de costo, tiempo y distancia
     * Permite al operador evaluar diferentes opciones de ruta antes de asignar una definitiva
     * Por ahora retorna una ruta directa simple, en el futuro podría ofrecer múltiples opciones
     * 
     * @param solicitudId ID de la solicitud
     * @return Lista de rutas tentativas con cálculos (200) o Not Found (404)
     */
    @GetMapping("/{solicitudId}/rutas/tentativas")
    public ResponseEntity<List<RutaTentativaDTO>> consultarRutasTentativas(@PathVariable Long solicitudId) {
        try {
            List<RutaTentativaDTO> rutas = rutaService.calcularRutasTentativas(solicitudId);
            return ResponseEntity.ok(rutas);
        } catch (RuntimeException e) {
            // Si la solicitud no existe o no tiene ruta asociada
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/solicitudes/{solicitudId}/asignar-ruta
     * RF#4: Asignar una ruta definitiva a una solicitud
     * Permite al operador seleccionar y confirmar una ruta tentativa como definitiva
     * Crea la ruta con sus tramos y cambia el estado de la solicitud a PROGRAMADA
     * 
     * @param solicitudId ID de la solicitud
     * @param rutaDTO DTO con la información de la ruta y sus tramos
     * @return Ruta creada (201) o Not Found (404)
     */
    @PostMapping("/{solicitudId}/asignar-ruta")
    public ResponseEntity<Ruta> asignarRuta(@PathVariable Long solicitudId, @Valid @RequestBody RutaCreateDTO rutaDTO) {
        try {
            Ruta rutaAsignada = rutaService.asignarRutaASolicitud(solicitudId, rutaDTO);
            return ResponseEntity.status(201).body(rutaAsignada);
        } catch (RuntimeException e) {
            // Si la solicitud no existe
            return ResponseEntity.notFound().build();
        }
    }
}