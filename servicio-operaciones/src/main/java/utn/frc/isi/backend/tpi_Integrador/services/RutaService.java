package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.repositories.RutaRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class RutaService {

    private final RutaRepository rutaRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    public List<Ruta> obtenerTodas() {
        return rutaRepository.findAll();
    }

    public Optional<Ruta> obtenerPorId(Long id) {
        return rutaRepository.findById(id);
    }

    public Ruta crearRuta(Ruta ruta) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que la solicitud asociada exista, calcular automáticamente cantidades, etc.
        // Por ahora, solo la guardamos.
        return rutaRepository.save(ruta);
    }

    public void eliminarRuta(Long id) {
        rutaRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarRutasPorSolicitud(Long solicitudId), optimizarRuta(Ruta ruta), etc.
}