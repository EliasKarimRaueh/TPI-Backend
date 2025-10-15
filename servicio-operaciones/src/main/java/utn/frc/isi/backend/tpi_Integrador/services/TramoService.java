package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;
import utn.frc.isi.backend.tpi_Integrador.repositories.TramoRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class TramoService {

    private final TramoRepository tramoRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public TramoService(TramoRepository tramoRepository) {
        this.tramoRepository = tramoRepository;
    }

    public List<Tramo> obtenerTodos() {
        return tramoRepository.findAll();
    }

    public Optional<Tramo> obtenerPorId(Long id) {
        return tramoRepository.findById(id);
    }

    public Tramo crearTramo(Tramo tramo) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que el camión esté disponible, calcular costos automáticamente, etc.
        // Por ahora, solo lo guardamos.
        return tramoRepository.save(tramo);
    }

    public Tramo actualizarTramo(Long id, Tramo tramo) {
        // Verificar si el tramo existe
        if (tramoRepository.existsById(id)) {
            tramo.setId(id); // Asegurar que el ID sea el correcto
            return tramoRepository.save(tramo);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarTramo(Long id) {
        tramoRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularTiempoViaje(Tramo tramo), etc.
}