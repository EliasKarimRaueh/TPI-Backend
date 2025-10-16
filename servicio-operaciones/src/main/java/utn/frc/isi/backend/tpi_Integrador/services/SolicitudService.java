package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.RutaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ClienteService clienteService;
    private final ContenedorService contenedorService;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final RutaRepository rutaRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public SolicitudService(SolicitudRepository solicitudRepository, 
                          ClienteService clienteService,
                          ContenedorService contenedorService,
                          ClienteRepository clienteRepository,
                          ContenedorRepository contenedorRepository,
                          RutaRepository rutaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.clienteService = clienteService;
        this.contenedorService = contenedorService;
        this.clienteRepository = clienteRepository;
        this.contenedorRepository = contenedorRepository;
        this.rutaRepository = rutaRepository;
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

    public Solicitud actualizarSolicitud(Long id, Solicitud solicitud) {
        // Verificar si la solicitud existe
        if (solicitudRepository.existsById(id)) {
            solicitud.setId(id); // Asegurar que el ID sea el correcto
            return solicitudRepository.save(solicitud);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarSolicitud(Long id) {
        solicitudRepository.deleteById(id);
    }

    /**
     * Crear nueva solicitud con lógica de orquestación completa
     * Este método maneja la creación coordinada de cliente, contenedor y solicitud
     * 
     * @param dto datos de la solicitud a crear
     * @return solicitud creada y guardada
     * @throws RuntimeException si el cliente no se encuentra o hay errores de validación
     */
    @Transactional
    public Solicitud crearNuevaSolicitud(SolicitudCreateDTO dto) {
        // Validación previa
        if (!dto.isValidClienteData()) {
            throw new IllegalArgumentException("Debe proporcionar clienteId O datos de cliente nuevo, pero no ambos");
        }

        // 1. Gestionar el Cliente
        Cliente cliente;
        if (dto.getClienteId() != null) {
            // Si se pasa un ID, buscar el cliente existente
            cliente = clienteService.obtenerPorId(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
        } else {
            // Si no, crear un nuevo cliente con los datos proporcionados
            if (dto.getCliente() == null) {
                throw new IllegalArgumentException("Debe proporcionar datos del cliente si no se especifica clienteId");
            }
            cliente = clienteService.crearCliente(dto.getCliente());
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
        
        // Calcular distancia aproximada usando fórmula de Haversine (simplificada)
        double distancia = calcularDistancia(dto.getLatitudOrigen(), dto.getLongitudOrigen(), 
                                           dto.getLatitudDestino(), dto.getLongitudDestino());
        nuevaRuta.setDistanciaKm(distancia);
        nuevaRuta.setTiempoEstimadoHoras((int) Math.ceil(distancia / 80)); // Aprox. 80 km/h
        
        Ruta rutaGuardada = rutaRepository.save(nuevaRuta);

        // 4. Crear la Solicitud
        Solicitud nuevaSolicitud = new Solicitud();
        nuevaSolicitud.setCliente(cliente);
        nuevaSolicitud.setContenedor(contenedorGuardado);
        nuevaSolicitud.setRuta(rutaGuardada);
        nuevaSolicitud.setObservaciones(dto.getObservaciones());
        nuevaSolicitud.setEstado("BORRADOR"); // Estado inicial según documento de diseño
        nuevaSolicitud.setFechaSolicitud(LocalDateTime.now().toString()); // Convertir a String
        
        // Calcular costos estimados
        double costoEstimado = distancia * 5.0; // $5 por km (ejemplo)
        double tiempoEstimado = distancia / 80.0; // horas
        
        nuevaSolicitud.setCostoEstimado(costoEstimado);
        nuevaSolicitud.setTiempoEstimado(tiempoEstimado);

        return solicitudRepository.save(nuevaSolicitud);
    }
    
    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2
     * @param lon2 Longitud del punto 2
     * @return Distancia en kilómetros
     */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA = 6371; // Radio de la Tierra en kilómetros

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = RADIO_TIERRA * c; // Distancia en kilómetros

        return distancia;
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), calcularCostoFinal(Solicitud solicitud), etc.
}