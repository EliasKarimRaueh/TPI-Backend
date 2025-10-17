package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para iniciar un tramo de transporte
 * Usado en RF#8 por el transportista para indicar el inicio del viaje
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InicioTramoDTO {
    
    // Opcional: si no se envía, se usa la fecha y hora actual del servidor.
    private LocalDateTime fechaHoraInicio;
}
