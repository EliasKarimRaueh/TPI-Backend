package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.time.LocalDateTime;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class Tramo {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    private String origen; // Dirección textual o ID de depósito de origen

    private String destino; // Dirección textual o ID de depósito de destino

    private String tipo; // Tipo de tramo (ej: ORIGEN_DEPOSITO, DEPOSITO_DESTINO)

    private String estado; // Estado del tramo (ej: ESTIMADO, ASIGNADO, INICIADO, FINALIZADO)

    private double costoAproximado; // Costo aproximado del tramo

    private double costoReal; // Costo real del tramo

    private LocalDateTime fechaHoraInicio; // Fecha y hora de inicio del tramo

    private LocalDateTime fechaHoraFin; // Fecha y hora de finalización del tramo

    @ManyToOne
    @JoinColumn(name = "camion_id") // Así se llamará la columna en la BD
    private CamionReference camion; // Referencia al camión asignado al tramo
}