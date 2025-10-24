package utn.frc.isi.backend.tpi_Integrador.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoUpdateDTO;
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
    public ResponseEntity<List<DepositoDTO>> obtenerTodos() {
        List<DepositoDTO> depositos = depositoService.obtenerTodos();
        return ResponseEntity.ok(depositos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositoDTO> obtenerPorId(@PathVariable Long id) {
        return depositoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DepositoDTO> crearDeposito(@Valid @RequestBody DepositoCreateDTO depositoCreateDTO) {
        DepositoDTO nuevoDeposito = depositoService.crearDeposito(depositoCreateDTO);
        return ResponseEntity.status(201).body(nuevoDeposito);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepositoDTO> actualizarDeposito(
            @PathVariable Long id, 
            @Valid @RequestBody DepositoUpdateDTO depositoUpdateDTO) {
        DepositoDTO depositoActualizado = depositoService.actualizarDeposito(id, depositoUpdateDTO);
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