package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosReservaConsulta;

public class ValidacionMedicoConOtraConsultaEnElMismoHorario {

    private ConsultaRepository consultaRepository;

    public void validar(DatosReservaConsulta datosReservaConsulta) {
        var medicoTieneOtraConsultaEnElMismoHorario = consultaRepository.existsByMedicoIdAndFecha(datosReservaConsulta.idMedico(), datosReservaConsulta.fecha());
        if (medicoTieneOtraConsultaEnElMismoHorario){
            throw new ValidacionException("El Médico ya tiene otra consulta en la misma fecha y hora");
        }
    }
}
