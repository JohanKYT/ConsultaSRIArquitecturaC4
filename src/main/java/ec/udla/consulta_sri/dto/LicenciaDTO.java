package ec.udla.consulta_sri.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.io.Serializable; // <--- IMPORTANTE

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
// AGREGAMOS "implements Serializable" PARA QUE REDIS NO EXPLOTE
public class LicenciaDTO implements Serializable {

    private static final long serialVersionUID = 1L; // <--- IMPORTANTE PARA SERIALIZACIÓN

    private String cedula;
    private int puntos;
    private String estado;
}