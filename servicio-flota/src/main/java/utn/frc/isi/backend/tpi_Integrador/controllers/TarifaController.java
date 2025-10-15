package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.models.Tarifa;
import utn.frc.isi.backend.tpi_Integrador.services.TarifaService;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping
    public ResponseEntity<List<Tarifa>> obtenerTodas() {
        List<Tarifa> tarifas = tarifaService.obtenerTodas();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> obtenerPorId(@PathVariable Long id) {
        return tarifaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tarifa> crearTarifa(@RequestBody Tarifa tarifa) {
        Tarifa nuevaTarifa = tarifaService.crearTarifa(tarifa);
        return ResponseEntity.status(201).body(nuevaTarifa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> actualizarTarifa(@PathVariable Long id, @RequestBody Tarifa tarifa) {
        Tarifa tarifaActualizada = tarifaService.actualizarTarifa(id, tarifa);
        if (tarifaActualizada != null) {
            return ResponseEntity.ok(tarifaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id) {
        tarifaService.eliminarTarifa(id);
        return ResponseEntity.noContent().build();
    }
}