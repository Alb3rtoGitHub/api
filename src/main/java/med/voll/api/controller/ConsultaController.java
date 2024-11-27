package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.consulta.DatosCancelarConsulta;
import med.voll.api.domain.consulta.DatosDetalleConsulta;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.consulta.ReservaDeConsultas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ReservaDeConsultas reservaDeConsultas;

    @PostMapping
    @Transactional
    public ResponseEntity reservar(@RequestBody @Valid DatosReservaConsulta datosReservaConsulta) {

        var datosDetalleConsulta = reservaDeConsultas.reservar(datosReservaConsulta);

        return ResponseEntity.ok(datosDetalleConsulta);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity cancelar(@RequestBody @Valid DatosCancelarConsulta datosCancelarConsulta) {
        reservaDeConsultas.cancelar(datosCancelarConsulta);
        return ResponseEntity.noContent().build();
    }
}
