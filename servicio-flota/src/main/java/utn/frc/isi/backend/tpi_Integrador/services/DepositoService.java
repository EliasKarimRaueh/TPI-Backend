package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.DepositoMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Deposito;
import utn.frc.isi.backend.tpi_Integrador.repositories.DepositoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class DepositoService {

    private final DepositoRepository depositoRepository;
    private final DepositoMapper depositoMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public DepositoService(DepositoRepository depositoRepository, DepositoMapper depositoMapper) {
        this.depositoRepository = depositoRepository;
        this.depositoMapper = depositoMapper;
    }

    public List<DepositoDTO> obtenerTodos() {
        return depositoRepository.findAll()
                .stream()
                .map(depositoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<DepositoDTO> obtenerPorId(Long id) {
        return depositoRepository.findById(id)
                .map(depositoMapper::toDTO);
    }

    public DepositoDTO crearDeposito(DepositoCreateDTO dto) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar coordenadas, verificar que no exista otro depósito en la misma ubicación, etc.
        Deposito deposito = depositoMapper.toEntity(dto);
        Deposito depositoGuardado = depositoRepository.save(deposito);
        return depositoMapper.toDTO(depositoGuardado);
    }

    public DepositoDTO actualizarDeposito(Long id, DepositoUpdateDTO dto) {
        // Buscar el depósito existente
        Optional<Deposito> depositoOpt = depositoRepository.findById(id);
        
        if (depositoOpt.isEmpty()) {
            return null; // Retorna null si no existe
        }
        
        Deposito deposito = depositoOpt.get();
        depositoMapper.updateEntity(dto, deposito);
        Deposito depositoActualizado = depositoRepository.save(deposito);
        return depositoMapper.toDTO(depositoActualizado);
    }

    public void eliminarDeposito(Long id) {
        depositoRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarDepositosPorUbicacion(double latitud, double longitud, double radio), etc.
}