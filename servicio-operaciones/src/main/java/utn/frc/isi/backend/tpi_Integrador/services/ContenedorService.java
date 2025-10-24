package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorPendienteDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;
    private final SolicitudRepository solicitudRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public ContenedorService(ContenedorRepository contenedorRepository,
                           SolicitudRepository solicitudRepository) {
        this.contenedorRepository = contenedorRepository;
        this.solicitudRepository = solicitudRepository;
    }

    public List<Contenedor> obtenerTodos() {
        return contenedorRepository.findAll();
    }

    public Optional<Contenedor> obtenerPorId(Long id) {
        return contenedorRepository.findById(id);
    }

    public Contenedor crearContenedor(Contenedor contenedor) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que peso y volumen sean positivos, establecer estado inicial, etc.
        // Por ahora, solo lo guardamos.
        return contenedorRepository.save(contenedor);
    }

    public Contenedor actualizarContenedor(Long id, Contenedor contenedor) {
        // Verificar si el contenedor existe
        if (contenedorRepository.existsById(id)) {
            contenedor.setId(id); // Asegurar que el ID sea el correcto
            return contenedorRepository.save(contenedor);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarContenedor(Long id) {
        contenedorRepository.deleteById(id);
    }
    
    /**
     * RF#2: Consultar estado del contenedor para seguimiento
     * Retorna información detallada del estado y ubicación actual del contenedor
     * 
     * @param id ID del contenedor
     * @return Optional con ContenedorEstadoDTO si el contenedor existe
     */
    public Optional<ContenedorEstadoDTO> consultarEstado(Long id) {
        return contenedorRepository.findById(id).map(contenedor -> {
            ContenedorEstadoDTO dto = new ContenedorEstadoDTO();
            dto.setId(contenedor.getId());
            dto.setNumero(contenedor.getNumero());
            dto.setEstado(contenedor.getEstado());
            
            // Obtener información del cliente
            if (contenedor.getCliente() != null) {
                dto.setNombreCliente(contenedor.getCliente().getNombre());
            }
            
            // Obtener ID de la solicitud asociada
            solicitudRepository.findByContenedor(contenedor).ifPresent(solicitud -> {
                dto.setSolicitudId(solicitud.getId());
            });
            
            // Determinar ubicación actual según el estado
            String ubicacion = determinarUbicacionPorEstado(contenedor.getEstado());
            dto.setUbicacionActual(ubicacion);
            
            return dto;
        });
    }
    
    /**
     * Método auxiliar para determinar la descripción de ubicación según el estado
     */
    private String determinarUbicacionPorEstado(String estado) {
        if (estado == null) {
            return "Estado desconocido";
        }
        
        switch (estado.toUpperCase()) {
            case "EN_ORIGEN":
                return "El contenedor se encuentra en la dirección de origen, listo para ser recogido.";
            case "EN_DEPOSITO":
                return "El contenedor está almacenado en un depósito intermedio de la ruta.";
            case "EN_VIAJE":
                return "El contenedor está en tránsito hacia el siguiente punto de la ruta.";
            case "ENTREGADO":
                return "El contenedor ha sido entregado exitosamente en la dirección de destino.";
            default:
                return "Estado: " + estado;
        }
    }
    
    /**
     * RF#5: Consultar contenedores pendientes de asignación
     * Retorna la lista de contenedores que aún no han sido entregados
     * 
     * @return Lista de ContenedorPendienteDTO con información básica de contenedores pendientes
     */
    public List<ContenedorPendienteDTO> consultarPendientes() {
        return contenedorRepository.findContenedoresPendientes()
                .stream()
                .map(this::mapToPendienteDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Método helper para mapear de Contenedor a ContenedorPendienteDTO
     */
    private ContenedorPendienteDTO mapToPendienteDTO(Contenedor contenedor) {
        ContenedorPendienteDTO dto = new ContenedorPendienteDTO();
        dto.setId(contenedor.getId());
        dto.setNumero(contenedor.getNumero());
        dto.setEstado(contenedor.getEstado());
        
        // Obtener nombre del cliente
        if (contenedor.getCliente() != null) {
            dto.setCliente(contenedor.getCliente().getNombre());
        }
        
        // Determinar ubicación según estado
        dto.setUbicacionActual(determinarUbicacionPorEstado(contenedor.getEstado()));
        
        // Buscar solicitud asociada
        solicitudRepository.findByContenedor(contenedor).ifPresent(solicitud -> {
            dto.setSolicitudId(solicitud.getId());
        });
        
        return dto;
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), buscarContenedoresPorCliente(Long clienteId), etc.
}