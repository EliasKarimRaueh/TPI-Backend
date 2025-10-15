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

    private String puntoInicio; // Punto de inicio del tramo

    private String puntoFin; // Punto de finalización del tramo

    private double distanciaKm; // Distancia del tramo en kilómetros

    private int tiempoEstimadoHoras; // Tiempo estimado en horas para el tramo

    @ManyToOne
    @JoinColumn(name = "ruta_id") // Así se llamará la columna en la BD
    private Ruta ruta; // Ruta a la que pertenece este tramo

    @ManyToOne
    @JoinColumn(name = "camion_reference_id") // Así se llamará la columna en la BD
    private CamionReference camionReference; // Referencia al camión asignado al tramo

    @ManyToOne
    @JoinColumn(name = "deposito_origen_id") // Depósito de origen del tramo (opcional)
    private DepositoReference depositoOrigen; // Referencia al depósito de origen

    @ManyToOne
    @JoinColumn(name = "deposito_destino_id") // Depósito de destino del tramo (opcional)
    private DepositoReference depositoDestino; // Referencia al depósito de destino
}