package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para finalizar un tramo de transporte
 * Usado en RF#8 por el transportista para indicar el fin del viaje
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinTramoDTO {
    
    // Opcional: si no se envía, se usa la fecha y hora actual del servidor.
    private LocalDateTime fechaHoraFin;
    
    // Observaciones opcionales sobre el tramo completado
    private String observaciones;
}
