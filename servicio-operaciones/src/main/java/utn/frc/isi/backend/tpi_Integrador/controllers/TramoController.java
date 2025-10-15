package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
}