package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Camion;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class CamionService {

    private final CamionRepository camionRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    public List<Camion> obtenerTodos() {
        return camionRepository.findAll();
    }

    public Optional<Camion> obtenerPorId(Long id) {
        return camionRepository.findById(id);
    }

    public Camion crearCamion(Camion camion) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: antes de guardar, verificar que la patente no exista.
        // Por ahora, solo lo guardamos.
        return camionRepository.save(camion);
    }

    public Camion actualizarCamion(Long id, Camion camion) {
        // Verificar si el camión existe
        if (camionRepository.existsById(id)) {
            camion.setId(id); // Asegurar que el ID sea el correcto
            return camionRepository.save(camion);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarCamion(Long id) {
        camionRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarDisponibilidad(Long id, boolean disponible), etc.
}