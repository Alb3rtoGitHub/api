package med.voll.api.domain.consulta.validaciones.reservas;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteSinOtraConsultaEnElMismoDia implements ValidadorDeConsultas{

    @Autowired
    private ConsultaRepository consultaRepository;

    public void validar(DatosReservaConsulta datosReservaConsulta) {
        var primerHorario = datosReservaConsulta.fecha().withHour(7);
        var ultimoHorario = datosReservaConsulta.fecha().withHour(18);
        var pacienteTieneOtraConsultaEnElDia = consultaRepository.existsByPacienteIdAndFechaBetween(datosReservaConsulta.idPaciente(), primerHorario, ultimoHorario);
        if (pacienteTieneOtraConsultaEnElDia) {
            throw new ValidacionException("El Paciente ya tiene una consulta reservada para ese día");
        }
    }
}
