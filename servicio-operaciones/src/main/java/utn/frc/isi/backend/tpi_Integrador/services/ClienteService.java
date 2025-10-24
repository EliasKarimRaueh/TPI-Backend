package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.mappers.ClienteMapper;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marca esta clase como un componente de servicio de Spring
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteDTO> obtenerTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClienteDTO> obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toDTO);
    }

    public ClienteDTO crearCliente(ClienteCreateDTO dto) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar formato de email, verificar que no exista otro cliente con el mismo email, etc.
        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteGuardado);
    }

    public ClienteDTO actualizarCliente(Long id, ClienteUpdateDTO dto) {
        // Buscar el cliente existente
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        
        if (clienteOpt.isEmpty()) {
            return null; // Retorna null si no existe
        }
        
        Cliente cliente = clienteOpt.get();
        clienteMapper.updateEntity(dto, cliente);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteActualizado);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarClientePorEmail(String email), validarDatosContacto(Cliente cliente), etc.
}