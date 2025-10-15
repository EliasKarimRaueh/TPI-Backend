package utn.frc.isi.backend.tpi_Integrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.models.Deposito;
import utn.frc.isi.backend.tpi_Integrador.services.DepositoService;

import java.util.List;

@RestController
@RequestMapping("/api/depositos")
public class DepositoController {

    private final DepositoService depositoService;

    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @GetMapping
    public ResponseEntity<List<Deposito>> obtenerTodos() {
        List<Deposito> depositos = depositoService.obtenerTodos();
        return ResponseEntity.ok(depositos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> obtenerPorId(@PathVariable Long id) {
        return depositoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Deposito> crearDeposito(@RequestBody Deposito deposito) {
        Deposito nuevoDeposito = depositoService.crearDeposito(deposito);
        return ResponseEntity.status(201).body(nuevoDeposito);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deposito> actualizarDeposito(@PathVariable Long id, @RequestBody Deposito deposito) {
        Deposito depositoActualizado = depositoService.actualizarDeposito(id, deposito);
        if (depositoActualizado != null) {
            return ResponseEntity.ok(depositoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeposito(@PathVariable Long id) {
        depositoService.eliminarDeposito(id);
        return ResponseEntity.noContent().build();
    }
}