package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.TarifaMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Tarifa;
import utn.frc.isi.backend.tpi_Integrador.repositories.TarifaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TarifaMapper tarifaMapper;

    public TarifaService(TarifaRepository tarifaRepository, TarifaMapper tarifaMapper) {
        this.tarifaRepository = tarifaRepository;
        this.tarifaMapper = tarifaMapper;
    }

    /**
     * Obtener la tarifa activa del sistema
     * @return Optional con la tarifa activa si existe
     */
    @Transactional(readOnly = true)
    public Optional<Tarifa> obtenerTarifaActiva() {
        return tarifaRepository.findByActiva(true);
    }

    /**
     * Obtener la tarifa activa del sistema como DTO
     * @return Optional con el DTO de la tarifa activa si existe
     */
    @Transactional(readOnly = true)
    public Optional<TarifaDTO> obtenerTarifaActivaDTO() {
        return tarifaRepository.findByActiva(true)
                .map(tarifaMapper::toDTO);
    }

    /**
     * Obtener todas las tarifas (históricas y activa) ordenadas por vigencia
     * @return Lista de todas las tarifas
     */
    @Transactional(readOnly = true)
    public List<Tarifa> obtenerTodas() {
        return tarifaRepository.findAllByOrderByVigenciaDesdeDesc();
    }

    /**
     * Obtener todas las tarifas como DTOs ordenadas por vigencia
     * @return Lista de DTOs de todas las tarifas
     */
    @Transactional(readOnly = true)
    public List<TarifaDTO> obtenerTodasDTO() {
        return tarifaRepository.findAllByOrderByVigenciaDesdeDesc().stream()
                .map(tarifaMapper::toDTO)
                .toList();
    }

    /**
     * Obtener tarifa por ID
     * @param id ID de la tarifa
     * @return Optional con la tarifa si existe
     */
    @Transactional(readOnly = true)
    public Optional<Tarifa> obtenerPorId(Long id) {
        return tarifaRepository.findById(id);
    }

    /**
     * Obtener tarifa por ID como DTO
     * @param id ID de la tarifa
     * @return Optional con el DTO de la tarifa si existe
     */
    @Transactional(readOnly = true)
    public Optional<TarifaDTO> obtenerPorIdDTO(Long id) {
        return tarifaRepository.findById(id)
                .map(tarifaMapper::toDTO);
    }

    /**
     * Crear nueva tarifa
     * IMPORTANTE: Al crear una tarifa nueva, se puede optar por desactivar las demás
     * @param tarifa la tarifa a crear
     * @return la tarifa creada
     */
    public Tarifa crearTarifa(Tarifa tarifa) {
        // Establecer fecha de vigencia si no está presente
        if (tarifa.getVigenciaDesde() == null) {
            tarifa.setVigenciaDesde(LocalDateTime.now());
        }
        
        // Lógica de negocio: Solo puede haber una tarifa activa a la vez
        if (tarifa.isActiva()) {
            desactivarTarifasActivas();
        }
        
        return tarifaRepository.save(tarifa);
    }

    /**
     * Crear nueva tarifa desde DTO
     * @param createDTO los datos para crear la tarifa
     * @return DTO de la tarifa creada
     */
    public TarifaDTO crearTarifaFromDTO(TarifaCreateDTO createDTO) {
        Tarifa tarifa = tarifaMapper.toEntity(createDTO);
        
        // Lógica de negocio: Solo puede haber una tarifa activa a la vez
        desactivarTarifasActivas();
        
        Tarifa tarifaGuardada = tarifaRepository.save(tarifa);
        return tarifaMapper.toDTO(tarifaGuardada);
    }

    /**
     * Actualizar tarifa existente
     * @param id ID de la tarifa a actualizar
     * @param tarifaActualizada datos de la tarifa actualizada
     * @return Optional con la tarifa actualizada si existe
     */
    public Optional<Tarifa> actualizarTarifa(Long id, Tarifa tarifaActualizada) {
        return tarifaRepository.findById(id)
                .map(tarifaExistente -> {
                    // Actualizar campos si están presentes
                    if (tarifaActualizada.getPrecioLitroCombustible() > 0) {
                        tarifaExistente.setPrecioLitroCombustible(tarifaActualizada.getPrecioLitroCombustible());
                    }
                    if (tarifaActualizada.getCargoGestionPorTramo() > 0) {
                        tarifaExistente.setCargoGestionPorTramo(tarifaActualizada.getCargoGestionPorTramo());
                    }
                    if (tarifaActualizada.getVigenciaHasta() != null) {
                        tarifaExistente.setVigenciaHasta(tarifaActualizada.getVigenciaHasta());
                    }
                    
                    // Lógica especial para activar/desactivar
                    if (tarifaActualizada.isActiva() && !tarifaExistente.isActiva()) {
                        desactivarTarifasActivas();
                        tarifaExistente.setActiva(true);
                    } else if (!tarifaActualizada.isActiva()) {
                        tarifaExistente.setActiva(false);
                    }
                    
                    return tarifaRepository.save(tarifaExistente);
                });
    }

    /**
     * Actualizar tarifa existente desde DTO
     * @param id ID de la tarifa a actualizar
     * @param updateDTO datos de actualización
     * @return Optional con DTO de la tarifa actualizada si existe
     */
    public Optional<TarifaDTO> actualizarTarifaFromDTO(Long id, TarifaUpdateDTO updateDTO) {
        return tarifaRepository.findById(id)
                .map(tarifaExistente -> {
                    // Lógica especial para activar/desactivar
                    if (updateDTO.getActiva() != null && updateDTO.getActiva() && !tarifaExistente.isActiva()) {
                        desactivarTarifasActivas();
                    }
                    
                    // Actualizar usando el mapper
                    tarifaMapper.updateEntityFromDTO(tarifaExistente, updateDTO);
                    
                    Tarifa tarifaGuardada = tarifaRepository.save(tarifaExistente);
                    return tarifaMapper.toDTO(tarifaGuardada);
                });
    }

    /**
     * Eliminar tarifa por ID
     * NOTA: Solo se puede eliminar si no está activa
     * @param id ID de la tarifa a eliminar
     * @return true si se eliminó, false si no existe o está activa
     */
    public boolean eliminarTarifa(Long id) {
        return tarifaRepository.findById(id)
                .map(tarifa -> {
                    if (tarifa.isActiva()) {
                        throw new IllegalStateException("No se puede eliminar una tarifa activa");
                    }
                    tarifaRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Desactivar todas las tarifas activas
     * Método de utilidad para asegurar que solo haya una tarifa activa
     */
    private void desactivarTarifasActivas() {
        tarifaRepository.findByActiva(true)
                .ifPresent(tarifaActiva -> {
                    tarifaActiva.setActiva(false);
                    tarifaActiva.setVigenciaHasta(LocalDateTime.now());
                    tarifaRepository.save(tarifaActiva);
                });
    }

    /**
     * Verificar si existe una tarifa activa en el sistema
     * @return true si existe una tarifa activa
     */
    @Transactional(readOnly = true)
    public boolean existeTarifaActiva() {
        return tarifaRepository.existsByActiva(true);
    }
}