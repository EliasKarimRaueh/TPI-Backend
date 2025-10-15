package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
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
    public ResponseEntity<List<Ruta>> obtenerTodas() {
        List<Ruta> rutas = rutaService.obtenerTodas();
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerPorId(@PathVariable Long id) {
        return rutaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ruta> crearRuta(@RequestBody Ruta ruta) {
        Ruta nuevaRuta = rutaService.crearRuta(ruta);
        return ResponseEntity.status(201).body(nuevaRuta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizarRuta(@PathVariable Long id, @RequestBody Ruta ruta) {
        Ruta rutaActualizada = rutaService.actualizarRuta(id, ruta);
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