package med.voll.api.domain.consulta.validaciones.reservas;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoConOtraConsultaEnElMismoHorario implements ValidadorDeConsultas{

    @Autowired
    private ConsultaRepository consultaRepository;

    public void validar(DatosReservaConsulta datosReservaConsulta) {
        var medicoTieneOtraConsultaEnElMismoHorario = consultaRepository.existsByMedicoIdAndFechaAndMotivoCancelacionIsNull(datosReservaConsulta.idMedico(), datosReservaConsulta.fecha());
        if (medicoTieneOtraConsultaEnElMismoHorario){
            throw new ValidacionException("El Médico ya tiene otra consulta en la misma fecha y hora");
        }
    }
}
