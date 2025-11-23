package ec.udla.consulta_sri.controller;

import ec.udla.consulta_sri.dto.LicenciaDTO;
import ec.udla.consulta_sri.dto.PersonaDTO;
import ec.udla.consulta_sri.dto.VehiculoDTO;
import ec.udla.consulta_sri.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
// CAMBIO CLAVE: Especificamos el puerto 3000 para que el navegador confíe plenamente
@CrossOrigin(origins = "http://localhost:3000")
public class ConsultaController {

    private final ConsultaService service;

    @GetMapping("/sri/validar/{ruc}")
    public ResponseEntity<Boolean> validarRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(service.existeContribuyente(ruc));
    }

    @GetMapping("/sri/persona/{ruc}")
    public ResponseEntity<?> obtenerPersona(@PathVariable String ruc) {
        try {
            return ResponseEntity.ok(service.obtenerPersona(ruc));
        } catch (Exception e) {
            // Esto envía el mensaje de error exacto al frontend
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/vehiculo/{placa}")
    public ResponseEntity<?> obtenerVehiculo(@PathVariable String placa) {
        try {
            return ResponseEntity.ok(service.obtenerVehiculo(placa));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Vehículo no encontrado");
        }
    }

    @GetMapping("/licencia/{cedula}")
    public ResponseEntity<?> obtenerLicencia(@PathVariable String cedula) {
        try {
            return ResponseEntity.ok(service.obtenerPuntosLicencia(cedula));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error consultando ANT: " + e.getMessage());
        }
    }
}