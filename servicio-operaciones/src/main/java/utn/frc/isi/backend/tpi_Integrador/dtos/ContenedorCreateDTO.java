package utn.frc.isi.backend.tpi_Integrador.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para crear un nuevo contenedor dentro de una solicitud
 * Según especificación del documento de diseño de API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorCreateDTO {

    @NotBlank(message = "El número del contenedor es requerido")
    private String numero;

    @NotBlank(message = "El tipo de contenedor es requerido")
    private String tipo; // STANDARD, REFRIGERADO, etc.

    @NotNull(message = "El peso es requerido")
    @Positive(message = "El peso debe ser positivo")
    private double peso; // en kg

    @NotNull(message = "El volumen es requerido")
    @Positive(message = "El volumen debe ser positivo")
    private double volumen; // en m³
}