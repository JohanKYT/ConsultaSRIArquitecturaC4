package ec.udla.consulta_sri;

import ec.udla.consulta_sri.dto.PersonaDTO;
import ec.udla.consulta_sri.service.ConsultaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private RestTemplate restTemplate; // Simulamos el cliente HTTP

    @InjectMocks
    private ConsultaService consultaService; // El servicio que vamos a probar

    @Test
    void obtenerPersona_DeberiaRetornarDatos_CuandoSriRespondeCorrectamente() {
        // 1. PREPARAR (GIVEN)
        String rucPrueba = "1790010937001";
        String urlEsperada = "https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc?&ruc=" + rucPrueba;

        // Simulamos la respuesta "sucia" del SRI (Map)
        Map<String, Object> datosMock = new HashMap<>();
        datosMock.put("numeroRuc", rucPrueba);
        datosMock.put("razonSocial", "LA FAVORITA C.A.");
        datosMock.put("estadoPersona", "ACTIVO"); // Usamos la clave que espera tu lógica corregida
        datosMock.put("claseContribuyente", "ESPECIAL");

        // El SRI devuelve un Array de Mapas
        Map[] respuestaArray = new Map[]{datosMock};

        // Le decimos a Mockito: "Cuando llamen a esta URL, devuelve este Array falso"
        when(restTemplate.getForObject(eq(urlEsperada), eq(Map[].class)))
                .thenReturn(respuestaArray);

        // 2. ACTUAR (WHEN)
        PersonaDTO resultado = consultaService.obtenerPersona(rucPrueba);

        // 3. VERIFICAR (THEN)
        assertNotNull(resultado);
        assertEquals("1790010937001", resultado.getRuc());
        assertEquals("LA FAVORITA C.A.", resultado.getNombre());
        assertEquals("ACTIVO", resultado.getEstadoContribuyente());
    }

    @Test
    void obtenerPersona_DeberiaLanzarExcepcion_CuandoRucNoExiste() {
        // 1. PREPARAR
        String rucFalso = "9999999999001";

        // Simulamos respuesta vacía o nula
        when(restTemplate.getForObject(anyString(), eq(Map[].class)))
                .thenReturn(new Map[]{});

        // 2. y 3. ACTUAR Y VERIFICAR
        // Esperamos que lance RuntimeException
        assertThrows(RuntimeException.class, () -> {
            consultaService.obtenerPersona(rucFalso);
        });
    }
}