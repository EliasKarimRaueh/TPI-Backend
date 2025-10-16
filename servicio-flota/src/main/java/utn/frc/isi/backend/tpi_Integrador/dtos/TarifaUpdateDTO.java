package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para actualizar una tarifa existente
 * Según el documento de diseño de API - todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaUpdateDTO {

    // OPCIONAL - Precio actual del litro de combustible
    private Double precioLitroCombustible;

    // OPCIONAL - Cargo fijo por tramo  
    private Double cargoGestionPorTramo;

    // OPCIONAL - Para cerrar vigencia de la tarifa
    private LocalDateTime vigenciaHasta;

    // OPCIONAL - Para activar/desactivar la tarifa
    private Boolean activa;
}