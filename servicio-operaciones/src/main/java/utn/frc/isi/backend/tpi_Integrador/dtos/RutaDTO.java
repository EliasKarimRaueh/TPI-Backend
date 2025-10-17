package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para mostrar información de una ruta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {
    
    private Long id; // ID de la ruta
    
    private String origen; // Punto de origen
    
    private String destino; // Punto de destino
    
    private double distanciaKm; // Distancia total en kilómetros
    
    private int tiempoEstimadoHoras; // Tiempo estimado en horas
}
