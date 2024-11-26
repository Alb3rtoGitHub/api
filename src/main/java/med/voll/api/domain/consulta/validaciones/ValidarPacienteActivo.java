package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.paciente.PacienteRepository;

public class ValidarPacienteActivo {

    private PacienteRepository pacienteRepository;

    public void validar(DatosReservaConsulta datosReservaConsulta) {
        var pacienteEstaActivo = pacienteRepository.findActivoById(datosReservaConsulta.idPaciente());
        if (!pacienteEstaActivo){
            throw new ValidacionException("Consulta no puede ser reservada con Paciente no Activo");
        }
    }
}
