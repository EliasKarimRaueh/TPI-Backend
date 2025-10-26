package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.CamionMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Camion;
import utn.frc.isi.backend.tpi_Integrador.repositories.CamionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class CamionService {

    private final CamionRepository camionRepository;
    private final CamionMapper camionMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public CamionService(CamionRepository camionRepository, CamionMapper camionMapper) {
        this.camionRepository = camionRepository;
        this.camionMapper = camionMapper;
    }

    public List<CamionDTO> obtenerTodos() {
        return camionRepository.findAll()
                .stream()
                .map(camionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CamionDTO> obtenerPorId(Long id) {
        return camionRepository.findById(id)
                .map(camionMapper::toDTO);
    }

    public CamionDTO crearCamion(CamionCreateDTO dto) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: antes de guardar, verificar que la patente no exista.
        Camion camion = camionMapper.toEntity(dto);
        Camion camionGuardado = camionRepository.save(camion);
        return camionMapper.toDTO(camionGuardado);
    }

    public CamionDTO actualizarCamion(Long id, CamionUpdateDTO dto) {
        // Buscar el camión existente
        Optional<Camion> camionOpt = camionRepository.findById(id);
        
        if (camionOpt.isEmpty()) {
            return null; // Retorna null si no existe
        }
        
        Camion camion = camionOpt.get();
        camionMapper.updateEntity(dto, camion);
        Camion camionActualizado = camionRepository.save(camion);
        return camionMapper.toDTO(camionActualizado);
    }

    public void eliminarCamion(Long id) {
        camionRepository.deleteById(id);
    }

    /**
     * Buscar camiones disponibles con filtros opcionales de capacidad
     * @param pesoMinimo filtro opcional para capacidad mínima de peso
     * @param volumenMinimo filtro opcional para capacidad mínima de volumen
     * @return lista de camiones que cumplen los criterios
     */
    public List<CamionDTO> buscarDisponibles(Double pesoMinimo, Double volumenMinimo) {
        // Crear especificación base: camión debe estar disponible
        Specification<Camion> spec = (root, query, cb) -> 
            cb.isTrue(root.get("disponible"));

        // Agregar filtro de peso mínimo si se proporciona
        if (pesoMinimo != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("capacidadPeso"), pesoMinimo)
            );
        }

        // Agregar filtro de volumen mínimo si se proporciona
        if (volumenMinimo != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("capacidadVolumen"), volumenMinimo)
            );
        }

        return camionRepository.findAll(spec)
                .stream()
                .map(camionMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza la disponibilidad de un camión
     * Usado cuando servicio-operaciones libera un camión al finalizar un tramo
     * 
     * @param id ID del camión a actualizar
     * @param disponible true para marcar como disponible, false para ocupado
     * @return Optional con CamionDTO actualizado, o vacío si no se encuentra
     */
    @Transactional
    public Optional<CamionDTO> actualizarDisponibilidad(Long id, boolean disponible) {
        Optional<Camion> camionOpt = camionRepository.findById(id);
        if (camionOpt.isPresent()) {
            Camion camion = camionOpt.get();
            camion.setDisponible(disponible);
            Camion camionGuardado = camionRepository.save(camion);
            return Optional.of(camionMapper.toDTO(camionGuardado));
        } else {
            return Optional.empty(); // Camión no encontrado
        }
    }
}