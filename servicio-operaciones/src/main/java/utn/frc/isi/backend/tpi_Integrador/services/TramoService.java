package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.AsignacionCamionDTO;
import utn.frc.isi.backend.tpi_Integrador.models.CamionReference;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionReferenceRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.TramoRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class TramoService {

    private final TramoRepository tramoRepository;
    private final CamionReferenceRepository camionReferenceRepository;
    private final SolicitudRepository solicitudRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public TramoService(TramoRepository tramoRepository, CamionReferenceRepository camionReferenceRepository, SolicitudRepository solicitudRepository) {
        this.tramoRepository = tramoRepository;
        this.camionReferenceRepository = camionReferenceRepository;
        this.solicitudRepository = solicitudRepository;
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
    
    /**
     * Asigna un camión a un tramo (RF#6)
     * Valida disponibilidad y capacidad del camión antes de asignar.
     * Actualiza el estado del tramo a "ASIGNADO" y marca el camión como no disponible.
     * 
     * @param tramoId ID del tramo
     * @param dto DTO con el ID del camión a asignar
     * @return Tramo actualizado con el camión asignado
     */
    @Transactional
    public Tramo asignarCamion(Long tramoId, AsignacionCamionDTO dto) {
        // PASO 1: Buscar el tramo
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));
        
        // Validar que el tramo no tenga ya un camión asignado
        if (tramo.getCamionReference() != null) {
            throw new RuntimeException("El tramo ya tiene un camión asignado");
        }
        
        // Validar que el tramo esté en estado PENDIENTE
        if (!"PENDIENTE".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo debe estar en estado PENDIENTE para asignar un camión");
        }
        
        // PASO 2: Obtener la referencia del camión
        // En el futuro, aquí llamaríamos al servicio-flota para obtener los datos actualizados
        // Por ahora, usamos la referencia local sincronizada
        CamionReference camionRef = camionReferenceRepository.findById(dto.getCamionId())
                .orElseThrow(() -> new RuntimeException("Referencia de Camión no encontrada con ID: " + dto.getCamionId()));
        
        // PASO 3: Validaciones de negocio
        
        // 3.1: Validar que el camión esté disponible
        if (!camionRef.isDisponible()) {
            throw new RuntimeException("El camión con dominio " + camionRef.getDominio() + " no está disponible");
        }
        
        // 3.2: Validar capacidad de peso y volumen
        // Necesitamos obtener el contenedor de la solicitud asociada a la ruta de este tramo
        if (tramo.getRuta() != null) {
            // Buscar la solicitud que tiene esta ruta
            Solicitud solicitud = solicitudRepository.findByRuta(tramo.getRuta())
                    .orElse(null);
            
            if (solicitud != null && solicitud.getContenedor() != null) {
                Contenedor contenedor = solicitud.getContenedor();
                
                // Validar peso
                if (camionRef.getCapacidadPeso() < contenedor.getPeso()) {
                    throw new RuntimeException(
                        String.format("El camión no tiene capacidad de peso suficiente. Requerido: %.2f kg, Disponible: %.2f kg",
                            contenedor.getPeso(), camionRef.getCapacidadPeso())
                    );
                }
                
                // Validar volumen
                if (camionRef.getCapacidadVolumen() < contenedor.getVolumen()) {
                    throw new RuntimeException(
                        String.format("El camión no tiene capacidad de volumen suficiente. Requerido: %.2f m³, Disponible: %.2f m³",
                            contenedor.getVolumen(), camionRef.getCapacidadVolumen())
                    );
                }
            }
        }
        
        // PASO 4: Asignar el camión y actualizar estados
        tramo.setCamionReference(camionRef);
        tramo.setEstado("ASIGNADO");
        
        // PASO 5: Marcar el camión como no disponible
        // En el futuro, aquí llamaríamos a: PATCH /api/camiones/{id}/disponibilidad en servicio-flota
        // Por ahora, actualizamos la referencia local
        camionRef.setDisponible(false);
        camionReferenceRepository.save(camionRef);
        
        // PASO 6: Guardar y retornar el tramo actualizado
        return tramoRepository.save(tramo);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularTiempoViaje(Tramo tramo), etc.
}