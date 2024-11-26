package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.DatosReservaConsulta;

import java.time.DayOfWeek;

public class ValidacionFueraHorarioConsultas {

    public void validar(DatosReservaConsulta datosReservaConsulta) {

        var fechaConsulta = datosReservaConsulta.fecha();
        var domingo = fechaConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY); // verifica si el dia de la semana es domingo
        var horarioAntesDeAperturaClinica = fechaConsulta.getHour() < 7;
        var horarioDespuesDeCierreClinica = fechaConsulta.getHour() > 18;
        if (domingo || horarioAntesDeAperturaClinica || horarioDespuesDeCierreClinica) {
            throw new ValidacionException("Horario fuera del horario de Atención de la Clínica");
        }
    }
}
