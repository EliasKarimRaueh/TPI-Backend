package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un Depósito existente
 * Todos los campos son opcionales para permitir actualizaciones parciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoUpdateDTO {
    
    private String nombre;
    
    private String direccion;
    
    private Double latitud;
    
    private Double longitud;
    
    @Positive(message = "El costo de estadía diaria debe ser positivo")
    private Double costoEstadiaDiaria;
}
