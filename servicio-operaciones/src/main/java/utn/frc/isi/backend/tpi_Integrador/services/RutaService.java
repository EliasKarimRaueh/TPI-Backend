package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.Coordenada;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaTentativaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TramoTentativoDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;
import utn.frc.isi.backend.tpi_Integrador.models.Solicitud;
import utn.frc.isi.backend.tpi_Integrador.repositories.RutaRepository;
import utn.frc.isi.backend.tpi_Integrador.repositories.SolicitudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class RutaService {

    private final RutaRepository rutaRepository;
    private final SolicitudRepository solicitudRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public RutaService(RutaRepository rutaRepository, SolicitudRepository solicitudRepository) {
        this.rutaRepository = rutaRepository;
        this.solicitudRepository = solicitudRepository;
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

    public Ruta actualizarRuta(Long id, Ruta ruta) {
        // Verificar si la ruta existe
        if (rutaRepository.existsById(id)) {
            ruta.setId(id); // Asegurar que el ID sea el correcto
            return rutaRepository.save(ruta);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarRuta(Long id) {
        rutaRepository.deleteById(id);
    }
    
    /**
     * Calcula rutas tentativas para una solicitud específica.
     * Genera propuestas de rutas con información detallada de tramos, costos y tiempos.
     * 
     * @param solicitudId ID de la solicitud
     * @return Lista de rutas tentativas con sus respectivos tramos
     */
    public List<RutaTentativaDTO> calcularRutasTentativas(Long solicitudId) {
        // Buscar la solicitud
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + solicitudId));
        
        // Verificar que tenga una ruta asignada
        Ruta ruta = solicitud.getRuta();
        if (ruta == null) {
            throw new RuntimeException("La solicitud no tiene una ruta asignada");
        }
        
        List<RutaTentativaDTO> rutasTentativas = new ArrayList<>();
        
        // Por ahora, generamos una ruta directa simple
        // En el futuro, aquí se podrían calcular múltiples rutas alternativas con depósitos intermedios
        
        // Calcular distancia usando la fórmula de Haversine
        double distanciaKm = calcularDistanciaHaversine(
            ruta.getLatitudOrigen(), 
            ruta.getLongitudOrigen(),
            ruta.getLatitudDestino(), 
            ruta.getLongitudDestino()
        );
        
        // Estimar tiempo (asumiendo 80 km/h promedio)
        double tiempoHoras = ruta.getTiempoEstimadoHoras();
        
        // Estimar costo ($5 por km como ejemplo)
        double costoEstimado = distanciaKm * 5.0;
        
        // Crear objeto Coordenada para punto inicio
        Coordenada puntoInicio = new Coordenada();
        puntoInicio.setLatitud(ruta.getLatitudOrigen());
        puntoInicio.setLongitud(ruta.getLongitudOrigen());
        
        // Crear objeto Coordenada para punto fin
        Coordenada puntoFin = new Coordenada();
        puntoFin.setLatitud(ruta.getLatitudDestino());
        puntoFin.setLongitud(ruta.getLongitudDestino());
        
        // Crear el tramo único (ruta directa)
        TramoTentativoDTO tramo = new TramoTentativoDTO();
        tramo.setOrden(1);
        tramo.setTipo("ORIGEN-DESTINO");
        tramo.setPuntoInicio(puntoInicio);
        tramo.setPuntoFin(puntoFin);
        tramo.setDistanciaKm(distanciaKm);
        tramo.setTiempoEstimadoHoras(tiempoHoras);
        tramo.setCostoAproximado(costoEstimado);
        tramo.setObservaciones("Ruta directa sin paradas intermedias");
        
        // Crear la ruta tentativa
        RutaTentativaDTO rutaTentativa = new RutaTentativaDTO();
        rutaTentativa.setTramos(List.of(tramo));
        rutaTentativa.setCostoEstimadoTotal(costoEstimado);
        rutaTentativa.setTiempoEstimadoTotal(tiempoHoras);
        rutaTentativa.setDistanciaTotal(distanciaKm);
        rutaTentativa.setCantidadTramos(1);
        rutaTentativa.setCantidadDepositos(0);
        rutaTentativa.setTipoRuta("DIRECTA");
        rutaTentativa.setDescripcion("Ruta directa de " + String.format("%.2f", distanciaKm) + " km sin paradas intermedias");
        
        rutasTentativas.add(rutaTentativa);
        
        return rutasTentativas;
    }
    
    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine.
     * 
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2
     * @param lon2 Longitud del punto 2
     * @return Distancia en kilómetros
     */
    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA_KM = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return RADIO_TIERRA_KM * c;
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarRutasPorSolicitud(Long solicitudId), optimizarRuta(Ruta ruta), etc.
}