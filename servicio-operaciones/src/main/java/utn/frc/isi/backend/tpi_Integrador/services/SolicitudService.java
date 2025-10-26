package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.SolicitudMapper;
import utn.frc.isi.backend.tpi_Integrador.mappers.RutaMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.RutaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ClienteService clienteService;
    private final ContenedorService contenedorService;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final RutaRepository rutaRepository;
    private final SolicitudMapper solicitudMapper;
    private final RutaMapper rutaMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public SolicitudService(SolicitudRepository solicitudRepository, 
                          ClienteService clienteService,
                          ContenedorService contenedorService,
                          ClienteRepository clienteRepository,
                          ContenedorRepository contenedorRepository,
                          RutaRepository rutaRepository,
                          SolicitudMapper solicitudMapper,
                          RutaMapper rutaMapper) {
        this.solicitudRepository = solicitudRepository;
        this.clienteService = clienteService;
        this.contenedorService = contenedorService;
        this.clienteRepository = clienteRepository;
        this.contenedorRepository = contenedorRepository;
        this.rutaRepository = rutaRepository;
        this.solicitudMapper = solicitudMapper;
        this.rutaMapper = rutaMapper;
    }

    /**
     * Obtiene todas las solicitudes
     * @return Lista de SolicitudDTO
     */
    public List<SolicitudDTO> obtenerTodas() {
        return solicitudRepository.findAll()
                .stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una solicitud por ID
     * @param id ID de la solicitud
     * @return Optional con SolicitudDTO si existe
     */
    public Optional<SolicitudDTO> obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .map(solicitudMapper::toDTO);
    }

    /**
     * Actualiza una solicitud existente
     * @param id ID de la solicitud a actualizar
     * @param dto DTO con los campos a actualizar
     * @return SolicitudDTO actualizada o null si no existe
     */
    public SolicitudDTO actualizarSolicitud(Long id, SolicitudUpdateDTO dto) {
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(id);
        if (solicitudOpt.isPresent()) {
            Solicitud solicitud = solicitudOpt.get();
            solicitudMapper.updateEntity(dto, solicitud);
            Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
            return solicitudMapper.toDTO(solicitudActualizada);
        }
        return null;
    }

    public void eliminarSolicitud(Long id) {
        solicitudRepository.deleteById(id);
    }

    /**
     * Crear nueva solicitud con lógica de orquestación completa
     * Este método maneja la creación coordinada de cliente, contenedor y solicitud
     * 
     * @param dto datos de la solicitud a crear
     * @return SolicitudDTO de la solicitud creada y guardada
     * @throws RuntimeException si el cliente no se encuentra o hay errores de validación
     */
    @Transactional
    public SolicitudDTO crearNuevaSolicitud(SolicitudCreateDTO dto) {
        // Validación previa
        if (!dto.isValidClienteData()) {
            throw new IllegalArgumentException("Debe proporcionar clienteId O datos de cliente nuevo, pero no ambos");
        }

        // 1. Gestionar el Cliente
        Cliente cliente;
        if (dto.getClienteId() != null) {
            // Si se pasa un ID, buscar el cliente existente directamente del repository
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
        } else {
            // Si no, crear un nuevo cliente con los datos proporcionados
            if (dto.getCliente() == null) {
                throw new IllegalArgumentException("Debe proporcionar datos del cliente si no se especifica clienteId");
            }
            // Guardar el cliente nuevo directamente con el repository
            cliente = clienteRepository.save(dto.getCliente());
        }

        // 2. Crear el Contenedor
        Contenedor nuevoContenedor = new Contenedor();
        nuevoContenedor.setNumero(dto.getContenedor().getNumero());
        nuevoContenedor.setTipo(dto.getContenedor().getTipo());
        nuevoContenedor.setPeso(dto.getContenedor().getPeso());
        nuevoContenedor.setVolumen(dto.getContenedor().getVolumen());
        nuevoContenedor.setCliente(cliente); // Asociar el contenedor al cliente
        nuevoContenedor.setEstado("EN_ORIGEN"); // Estado inicial del contenedor
        
        Contenedor contenedorGuardado = contenedorRepository.save(nuevoContenedor);

        // 3. Crear la Ruta
        Ruta nuevaRuta = new Ruta();
        nuevaRuta.setOrigen(dto.getDireccionOrigen());
        nuevaRuta.setDestino(dto.getDireccionDestino());
        nuevaRuta.setLatitudOrigen(dto.getLatitudOrigen());
        nuevaRuta.setLongitudOrigen(dto.getLongitudOrigen());
        nuevaRuta.setLatitudDestino(dto.getLatitudDestino());
        nuevaRuta.setLongitudDestino(dto.getLongitudDestino());
        
        // La distancia y tiempo se calcularán cuando se consulten las rutas tentativas (RF#3)
        // usando Google Maps Distance Matrix API
        nuevaRuta.setDistanciaKm(0.0);
        nuevaRuta.setTiempoEstimadoHoras(0);
        
        Ruta rutaGuardada = rutaRepository.save(nuevaRuta);

        // 4. Crear la Solicitud
        Solicitud nuevaSolicitud = new Solicitud();
        nuevaSolicitud.setCliente(cliente);
        nuevaSolicitud.setContenedor(contenedorGuardado);
        nuevaSolicitud.setRuta(rutaGuardada);
        nuevaSolicitud.setObservaciones(dto.getObservaciones());
        nuevaSolicitud.setEstado("BORRADOR"); // Estado inicial según documento de diseño
        nuevaSolicitud.setFechaSolicitud(LocalDateTime.now().toString()); // Convertir a String
        
        // Los costos y tiempos se calcularán cuando se consulten las rutas tentativas
        nuevaSolicitud.setCostoEstimado(0.0);
        nuevaSolicitud.setTiempoEstimado(0.0);

        Solicitud solicitudGuardada = solicitudRepository.save(nuevaSolicitud);
        return solicitudMapper.toDTO(solicitudGuardada);
    }
    
    /**
     * RF#2: Consultar estado completo de una solicitud de transporte
     * Incluye estado del contenedor, ruta e historial de tramos
     * 
     * @param id ID de la solicitud
     * @return Optional con SolicitudEstadoDTO si la solicitud existe
     */
    public Optional<SolicitudEstadoDTO> consultarEstadoSolicitud(Long id) {
        return solicitudRepository.findById(id).map(solicitud -> {
            SolicitudEstadoDTO estadoDTO = new SolicitudEstadoDTO();
            
            // Información básica de la solicitud
            estadoDTO.setId(solicitud.getId());
            estadoDTO.setEstado(solicitud.getEstado());
            
            // Estado del contenedor
            if (solicitud.getContenedor() != null) {
                ContenedorEstadoDTO contenedorEstado = new ContenedorEstadoDTO();
                contenedorEstado.setId(solicitud.getContenedor().getId());
                contenedorEstado.setNumero(solicitud.getContenedor().getNumero());
                contenedorEstado.setEstado(solicitud.getContenedor().getEstado());
                
                // Ubicación del contenedor
                String ubicacion = determinarUbicacionContenedor(solicitud.getContenedor().getEstado());
                contenedorEstado.setUbicacionActual(ubicacion);
                
                if (solicitud.getCliente() != null) {
                    contenedorEstado.setNombreCliente(solicitud.getCliente().getNombre());
                }
                contenedorEstado.setSolicitudId(solicitud.getId());
                
                estadoDTO.setContenedor(contenedorEstado);
            }
            
            // Información de la ruta
            if (solicitud.getRuta() != null) {
                RutaDTO rutaDTO = rutaMapper.toDTO(solicitud.getRuta());
                rutaDTO.setSolicitudId(solicitud.getId());
                estadoDTO.setRutaActual(rutaDTO);
            }
            
            // Historial de tramos (por ahora vacío, se completará cuando implementemos tramos)
            estadoDTO.setHistorialTramos(new ArrayList<>());
            
            // Calcular progreso basado en el estado
            double progreso = calcularProgreso(solicitud.getEstado());
            estadoDTO.setProgreso(progreso);
            
            // ETA al destino
            String eta = calcularETA(solicitud);
            estadoDTO.setEtaDestino(eta);
            
            return estadoDTO;
        });
    }
    
    /**
     * Determina la ubicación textual del contenedor según su estado
     */
    private String determinarUbicacionContenedor(String estado) {
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
     * Calcula el porcentaje de progreso basado en el estado de la solicitud
     */
    private double calcularProgreso(String estado) {
        if (estado == null) {
            return 0.0;
        }
        
        switch (estado.toUpperCase()) {
            case "BORRADOR":
                return 10.0; // 10% - Solicitud creada
            case "PROGRAMADA":
                return 25.0; // 25% - Ruta asignada
            case "EN_TRANSITO":
                return 60.0; // 60% - En viaje
            case "ENTREGADA":
                return 100.0; // 100% - Completada
            default:
                return 0.0;
        }
    }
    
    /**
     * Calcula el tiempo estimado de llegada (ETA) al destino
     */
    private String calcularETA(Solicitud solicitud) {
        if (solicitud.getEstado() == null) {
            return "No disponible";
        }
        
        switch (solicitud.getEstado().toUpperCase()) {
            case "BORRADOR":
                return "Pendiente de programación";
            case "PROGRAMADA":
                return "Esperando inicio de transporte";
            case "EN_TRANSITO":
                if (solicitud.getTiempoEstimado() > 0) {
                    int horas = (int) solicitud.getTiempoEstimado();
                    return "Aproximadamente " + horas + " horas";
                }
                return "Calculando...";
            case "ENTREGADA":
                return "Ya entregado";
            default:
                return "No disponible";
        }
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularCostoFinal(Solicitud solicitud), etc.
}