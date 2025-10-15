package utn.frc.isi.backend.tpi_Integrador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity // Marca esta clase como una entidad que se mapeará a una tabla en la BD
@Data   // Genera automáticamente getters, setters, toString, etc.
public class Ruta {

    @Id // Define el campo 'id' como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que el ID será autogenerado por la base de datos
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitud_id") // Así se llamará la columna en la BD
    private Solicitud solicitud; // Solicitud asociada a la ruta

    private int cantidadTramos; // Cantidad total de tramos en la ruta

    private int cantidadDepositos; // Cantidad de depósitos involucrados en la ruta
}