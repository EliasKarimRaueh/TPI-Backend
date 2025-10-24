package utn.frc.isi.backend.tpi_Integrador.mappers;

import org.springframework.stereotype.Component;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.models.Ruta;

/**
 * Mapper para convertir entre entidades Ruta y DTOs
 * Nota: RutaCreateDTO se maneja especialmente en RutaService.asignarRutaASolicitud()
 * porque requiere crear también los tramos (RF#4)
 */
@Component
public class RutaMapper {

    /**
     * Convierte una entidad Ruta a RutaDTO
     * @param ruta entidad a convertir
     * @return RutaDTO o null si la ruta es null
     */
    public RutaDTO toDTO(Ruta ruta) {
        if (ruta == null) {
            return null;
        }

        RutaDTO dto = new RutaDTO();
        dto.setId(ruta.getId());
        dto.setOrigen(ruta.getOrigen());
        dto.setDestino(ruta.getDestino());
        dto.setDistanciaKm(ruta.getDistanciaKm());
        dto.setTiempoEstimadoHoras(ruta.getTiempoEstimadoHoras());

        return dto;
    }

    /**
     * Actualiza una entidad Ruta existente con datos de RutaUpdateDTO
     * Solo actualiza campos que no son null en el DTO (actualización parcial)
     * @param dto DTO con los campos a actualizar
     * @param ruta entidad existente a actualizar
     */
    public void updateEntity(RutaUpdateDTO dto, Ruta ruta) {
        if (dto.getOrigen() != null) {
            ruta.setOrigen(dto.getOrigen());
        }
        if (dto.getDestino() != null) {
            ruta.setDestino(dto.getDestino());
        }
        if (dto.getLatitudOrigen() != null) {
            ruta.setLatitudOrigen(dto.getLatitudOrigen());
        }
        if (dto.getLongitudOrigen() != null) {
            ruta.setLongitudOrigen(dto.getLongitudOrigen());
        }
        if (dto.getLatitudDestino() != null) {
            ruta.setLatitudDestino(dto.getLatitudDestino());
        }
        if (dto.getLongitudDestino() != null) {
            ruta.setLongitudDestino(dto.getLongitudDestino());
        }
        if (dto.getDistanciaKm() != null) {
            ruta.setDistanciaKm(dto.getDistanciaKm());
        }
        if (dto.getTiempoEstimadoHoras() != null) {
            ruta.setTiempoEstimadoHoras(dto.getTiempoEstimadoHoras());
        }
    }
}
