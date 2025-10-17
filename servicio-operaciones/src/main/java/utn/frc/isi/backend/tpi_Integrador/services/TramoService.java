package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.AsignacionCamionDTO;
import utn.frc.isi.backend.tpi_Integrador.models.CamionReference;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.models.Tramo;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionReferenceRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.TramoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class TramoService {

    private final TramoRepository tramoRepository;
    private final CamionReferenceRepository camionReferenceRepository;
    private final SolicitudRepository solicitudRepository;
    private final ContenedorRepository contenedorRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public TramoService(TramoRepository tramoRepository, CamionReferenceRepository camionReferenceRepository, SolicitudRepository solicitudRepository, ContenedorRepository contenedorRepository) {
        this.tramoRepository = tramoRepository;
        this.camionReferenceRepository = camionReferenceRepository;
        this.solicitudRepository = solicitudRepository;
        this.contenedorRepository = contenedorRepository;
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
    
    /**
     * Inicia un tramo de transporte (RF#8)
     * El transportista marca el inicio del viaje.
     * Actualiza el estado del tramo y del contenedor.
     * 
     * @param tramoId ID del tramo a iniciar
     * @return Tramo actualizado
     */
    @Transactional
    public Tramo iniciarTramo(Long tramoId) {
        // 1. Buscar el tramo
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));
        
        // 2. Validaciones de negocio
        if (!"ASIGNADO".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo no está en estado 'ASIGNADO'. Estado actual: " + tramo.getEstado());
        }
        
        // 3. Actualizar estado del Tramo
        tramo.setEstado("INICIADO");
        tramo.setFechaRealInicio(LocalDateTime.now());
        
        // 4. Buscar solicitud asociada para actualizar contenedor
        Solicitud solicitud = solicitudRepository.findByRuta(tramo.getRuta())
                .orElseThrow(() -> new RuntimeException("No se encontró solicitud asociada al tramo"));
        
        // 5. Actualizar estado del Contenedor
        Contenedor contenedor = solicitud.getContenedor();
        contenedor.setEstado("EN_VIAJE");
        contenedorRepository.save(contenedor);
        
        // 6. Actualizar estado de la Solicitud si no está ya en tránsito
        if (!"EN_TRANSITO".equals(solicitud.getEstado())) {
            solicitud.setEstado("EN_TRANSITO");
            solicitudRepository.save(solicitud);
        }
        
        // 7. Guardar y retornar el tramo actualizado
        return tramoRepository.save(tramo);
    }
    
    /**
     * Finaliza un tramo de transporte (RF#8)
     * El transportista marca el fin del viaje.
     * Actualiza el estado del tramo y, si es el último, del contenedor y solicitud.
     * 
     * @param tramoId ID del tramo a finalizar
     * @return Tramo actualizado
     */
    @Transactional
    public Tramo finalizarTramo(Long tramoId) {
        // 1. Buscar el tramo
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));
        
        // 2. Validaciones de negocio
        if (!"INICIADO".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo no está en estado 'INICIADO'. Estado actual: " + tramo.getEstado());
        }
        
        // 3. Actualizar estado del Tramo
        tramo.setEstado("FINALIZADO");
        tramo.setFechaRealFin(LocalDateTime.now());
        
        // 4. Buscar solicitud asociada
        Solicitud solicitud = solicitudRepository.findByRuta(tramo.getRuta())
                .orElseThrow(() -> new RuntimeException("No se encontró solicitud asociada al tramo"));
        
        // 5. Verificar si es el último tramo de la ruta
        List<Tramo> tramosRuta = tramoRepository.findByRutaOrderByOrdenAsc(tramo.getRuta());
        boolean todosFinalizados = tramosRuta.stream()
                .allMatch(t -> "FINALIZADO".equals(t.getEstado()) || t.getId().equals(tramoId));
        
        // 6. Si todos los tramos están finalizados, actualizar contenedor y solicitud
        if (todosFinalizados) {
            Contenedor contenedor = solicitud.getContenedor();
            contenedor.setEstado("ENTREGADO");
            contenedorRepository.save(contenedor);
            
            solicitud.setEstado("ENTREGADA");
            solicitudRepository.save(solicitud);
        } else {
            // Si no es el último, marcar contenedor como en depósito intermedio
            Contenedor contenedor = solicitud.getContenedor();
            if (tramo.getTipo().contains("DEPOSITO")) {
                contenedor.setEstado("EN_DEPOSITO");
                contenedorRepository.save(contenedor);
            }
        }
        
        // 7. Liberar el camión
        if (tramo.getCamionReference() != null) {
            CamionReference camion = tramo.getCamionReference();
            camion.setDisponible(true);
            camionReferenceRepository.save(camion);
        }
        
        // 8. Guardar y retornar el tramo actualizado
        return tramoRepository.save(tramo);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularTiempoViaje(Tramo tramo), etc.
}