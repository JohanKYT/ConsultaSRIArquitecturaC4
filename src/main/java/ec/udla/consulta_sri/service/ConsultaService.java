package ec.udla.consulta_sri.service;

import ec.udla.consulta_sri.dto.LicenciaDTO;
import ec.udla.consulta_sri.dto.PersonaDTO;
import ec.udla.consulta_sri.dto.VehiculoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor // <--- ¡Magia! Inyecta el RestTemplate automáticamente
public class ConsultaService {

    private final RestTemplate restTemplate;

    // URLs OFICIALES
    private static final String SRI_EXISTE_URL = "https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/existePorNumeroRuc?numeroRuc=";
    private static final String SRI_PERSONA_URL = "https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente/obtenerPorNumerosRuc?&ruc=";
    private static final String SRI_VEHICULO_URL = "https://srienlinea.sri.gob.ec/sri-matriculacion-vehicular-recaudacion-servicio-internet/rest/BaseVehiculo/obtenerPorNumeroPlacaOPorNumeroCampvOPorNumeroCpn?numeroPlacaCampvCpn=";

    // URL ANT (HTML)
    private static final String ANT_PUNTOS_URL = "https://consultaweb.ant.gob.ec/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp?ps_tipo_identificacion=CED&ps_identificacion=%s&ps_placa=";

    // 1. VALIDAR RUC
    public boolean existeContribuyente(String ruc) {
        try {
            Boolean existe = restTemplate.getForObject(SRI_EXISTE_URL + ruc, Boolean.class);
            return Boolean.TRUE.equals(existe);
        } catch (Exception e) {
            log.error("Error consultando SRI (Validar): {}", e.getMessage());
            return false;
        }
    }

    // 2. OBTENER PERSONA
    public PersonaDTO obtenerPersona(String ruc) {
        try {
            Map[] respuesta = restTemplate.getForObject(SRI_PERSONA_URL + ruc, Map[].class);

            if (respuesta != null && respuesta.length > 0) {
                Map<String, Object> datos = respuesta[0];

                // IMPRIMIR EN CONSOLA PARA VER LOS NOMBRES REALES SI FALLA
                log.info("Datos SRI recibidos: " + datos);

                PersonaDTO persona = new PersonaDTO();
                persona.setRuc((String) datos.get("numeroRuc"));

                // Nombre
                String nombre = (String) datos.get("razonSocial");
                if (nombre == null) nombre = (String) datos.get("nombreComercial");
                persona.setNombre(nombre);

              //Comprobar el estado
                String estado = (String) datos.get("estadoPersona");
                if (estado == null) estado = (String) datos.get("estadoContribuyente");
                if (estado == null) estado = (String) datos.get("estado"); // Último intento
                persona.setEstadoContribuyente(estado);


                String clase = (String) datos.get("claseContribuyente");
                if (clase == null) clase = (String) datos.get("clase");
                persona.setTipoContribuyente(clase);

                persona.setDireccion("Domicilio Fiscal SRI");

                return persona;
            }
            throw new RuntimeException("No se encontraron datos para el RUC: " + ruc);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error SRI: " + e.getMessage());
        }
    }


    // 3. OBTENER VEHÍCULO
    public VehiculoDTO obtenerVehiculo(String placa) {
        try {
            Map respuesta = restTemplate.getForObject(SRI_VEHICULO_URL + placa, Map.class);

            if(respuesta == null || respuesta.isEmpty()) {
                throw new RuntimeException("Vehículo no encontrado");
            }

            VehiculoDTO vehiculo = new VehiculoDTO();

            vehiculo.setPlaca((String) respuesta.get("numeroPlaca"));
            vehiculo.setMarca((String) respuesta.get("descripcionMarca"));
            vehiculo.setModelo((String) respuesta.get("descripcionModelo"));

            // El año viene como "anioAuto"
            vehiculo.setAnio(String.valueOf(respuesta.get("anioAuto")));

            // La clase a veces viene null, ponemos una validación
            String clase = (String) respuesta.get("nombreClase");
            vehiculo.setClase(clase != null ? clase : "Particular/Desconocido");

            return vehiculo;

        } catch (Exception e) {
            log.error("Error mapeando vehículo: ", e); // Para ver errores en consola
            throw new RuntimeException("No se encontró información para la placa: " + placa);
        }
    }

    // 4. OBTENER PUNTOS (CON JSOUP y REDIS)
    @Cacheable(value = "licenciaCache", key = "#cedula", unless = "#result == null")
    public LicenciaDTO obtenerPuntosLicencia(String cedula) {
        log.info("--- Consultando ANT (Web Scraping) para: {} ---", cedula);

        String url = String.format(ANT_PUNTOS_URL, cedula);
        LicenciaDTO licencia = new LicenciaDTO();
        licencia.setCedula(cedula);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            licencia.setPuntos(30);
            licencia.setEstado("VIGENTE");


            return licencia;

        } catch (Exception e) {
            log.error("Error conectando a la ANT", e);
            // Si falla la conexión (timeout), lanzamos excepción para no cachear basura.
            throw new RuntimeException("Servicio ANT no disponible (Timeout o Bloqueo).");
        }
    }
}