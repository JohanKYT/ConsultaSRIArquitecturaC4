package ec.udla.consulta_sri.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonaDTO {
    // OJO: Los nombres aquí determinan cómo se llaman los setters

    private String ruc;                 // Genera: setRuc()
    private String nombre;              // Genera: setNombre()
    private String direccion;           // Genera: setDireccion()
    private String estadoContribuyente; // Genera: setEstadoContribuyente()
    private String tipoContribuyente;   // Genera: setTipoContribuyente()
}