package med.voll.api.domain.consulta.validaciones.cancelaciones;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosCancelarConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorHorarioConAnticipacion implements ValidadorCancelarConsulta {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Override
    public void validar(DatosCancelarConsulta datosCancelarConsulta) {
        var consulta = consultaRepository.getReferenceById(datosCancelarConsulta.idConsulta());
        var ahora = LocalDateTime.now();
        var diferenciaEnHoras = Duration.between(ahora, consulta.getFecha()).toHours();
        if (diferenciaEnHoras < 24) {
            throw new ValidacionException("La consulta sólo puede ser cancelada con una Anticipación mínima de 24Hs");
        }
    }

}
