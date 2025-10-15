package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Cliente;
import utn.frc.isi.backend.tpi_Integrador.repositories.ClienteRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente crearCliente(Cliente cliente) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar formato de email, verificar que no exista otro cliente con el mismo email, etc.
        // Por ahora, solo lo guardamos.
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Long id, Cliente cliente) {
        // Verificar si el cliente existe
        if (clienteRepository.existsById(id)) {
            cliente.setId(id); // Asegurar que el ID sea el correcto
            return clienteRepository.save(cliente);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarClientePorEmail(String email), validarDatosContacto(Cliente cliente), etc.
}