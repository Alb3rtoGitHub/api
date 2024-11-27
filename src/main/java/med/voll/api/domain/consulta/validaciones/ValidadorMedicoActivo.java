package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.medico.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoActivo implements ValidadorDeConsultas{

    @Autowired
    private MedicoRepository medicoRepository;

    public void validar(DatosReservaConsulta datosReservaConsulta) {
        // Elección del médico opcional
        if (datosReservaConsulta.idMedico() == null){
            return;
        }
        var medicoEstaActivo = medicoRepository.findActivoById(datosReservaConsulta.idMedico());
        if (!medicoEstaActivo){
            throw new ValidacionException("Consulta no puede ser reservada con Médico no Activo");
        }
    }
}
