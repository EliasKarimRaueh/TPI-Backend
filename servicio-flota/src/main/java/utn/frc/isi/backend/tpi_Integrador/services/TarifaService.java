package utn.frc.isi.backend.tpi_Integrador.services;

import org.springframework.stereotype.Service;
import utn.frc.isi.backend.tpi_Integrador.models.Tarifa;
import utn.frc.isi.backend.tpi_Integrador.repositories.TarifaRepository;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class TarifaService {

    private final TarifaRepository tarifaRepository;

    // Inyección de dependencias a través del constructor (práctica recomendada)
    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public List<Tarifa> obtenerTodas() {
        return tarifaRepository.findAll();
    }

    public Optional<Tarifa> obtenerPorId(Long id) {
        return tarifaRepository.findById(id);
    }

    public Tarifa crearTarifa(Tarifa tarifa) {
        // Aquí podríamos agregar lógica de negocio.
        // Por ejemplo: validar que el valor sea positivo, verificar que no exista otra tarifa del mismo tipo, etc.
        // Por ahora, solo la guardamos.
        return tarifaRepository.save(tarifa);
    }

    public void eliminarTarifa(Long id) {
        tarifaRepository.deleteById(id);
    }
    
    // Aquí se podrían agregar más métodos de negocio en el futuro,
    // como buscarTarifasPorTipo(String tipo), calcularCostoTotal(String tipo, double cantidad), etc.
}