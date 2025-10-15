package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Deposito;
import utn.frc.isi.backend.tpi_Integrador.repositories.DepositoRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class DepositoService {

    private final DepositoRepository depositoRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public DepositoService(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    public List<Deposito> obtenerTodos() {
        return depositoRepository.findAll();
    }

    public Optional<Deposito> obtenerPorId(Long id) {
        return depositoRepository.findById(id);
    }

    public Deposito crearDeposito(Deposito deposito) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar coordenadas, verificar que no exista otro depósito en la misma ubicación, etc.
        // Por ahora, solo lo guardamos.
        return depositoRepository.save(deposito);
    }

    public Deposito actualizarDeposito(Long id, Deposito deposito) {
        // Verificar si el depósito existe
        if (depositoRepository.existsById(id)) {
            deposito.setId(id); // Asegurar que el ID sea el correcto
            return depositoRepository.save(deposito);
        }
        return null; // Retorna null si no existe
    }

    public void eliminarDeposito(Long id) {
        depositoRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarDepositosPorUbicacion(double latitud, double longitud, double radio), etc.
}