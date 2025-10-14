package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public List<Solicitud> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    public Optional<Solicitud> obtenerPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    public Solicitud crearSolicitud(Solicitud solicitud) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: calcular costos estimados, validar que el contenedor y cliente existan, establecer estado inicial, etc.
        // Por ahora, solo la guardamos.
        return solicitudRepository.save(solicitud);
    }

    public void eliminarSolicitud(Long id) {
        solicitudRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularCostoFinal(Solicitud solicitud), etc.
}