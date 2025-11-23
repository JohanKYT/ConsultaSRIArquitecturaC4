package ec.udla.consulta_sri.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiculoDTO {
    private String placa;
    private String marca;
    private String modelo;

    // CAMBIO IMPORTANTE: Lo ponemos como String para evitar errores de conversión
    private String anio;

    private String clase;
}