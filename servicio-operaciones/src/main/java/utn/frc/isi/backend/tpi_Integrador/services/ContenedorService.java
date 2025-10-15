package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Contenedor;
import utn.frc.isi.backend.tpi_Integrador.repositories.ContenedorRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public ContenedorService(ContenedorRepository contenedorRepository) {
        this.contenedorRepository = contenedorRepository;
    }

    public List<Contenedor> obtenerTodos() {
        return contenedorRepository.findAll();
    }

    public Optional<Contenedor> obtenerPorId(Long id) {
        return contenedorRepository.findById(id);
    }

    public Contenedor crearContenedor(Contenedor contenedor) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que peso y volumen sean positivos, establecer estado inicial, etc.
        // Por ahora, solo lo guardamos.
        return contenedorRepository.save(contenedor);
    }

    public Contenedor actualizarContenedor(Long id, Contenedor contenedor) {
        // Verificar si el contenedor existe
        if (contenedorRepository.existsById(id)) {
            contenedor.setId(id); // Asegurar que el ID sea el correcto
            return contenedorRepository.save(contenedor);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarContenedor(Long id) {
        contenedorRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como actualizarEstado(Long id, String nuevoEstado), buscarContenedoresPorCliente(Long clienteId), etc.
}