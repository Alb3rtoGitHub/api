package med.voll.api.domain.consulta;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.consulta.validaciones.ValidadorDeConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaDeConsultas {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    List<ValidadorDeConsultas> validadores; // usar una lista de Interfaces, spring busca todas las clases que implementan esta interfaz y crea una lista

    public DatosDetalleConsulta reservar(DatosReservaConsulta datosReservaConsulta){

        if(!pacienteRepository.existsById(datosReservaConsulta.idPaciente())){
            throw new ValidacionException("No existe un Paciente con el id informado");
        }

        if (datosReservaConsulta.idMedico() != null && !medicoRepository.existsById(datosReservaConsulta.idMedico())){
            throw new ValidacionException("No existe un Médico con el id informado");
        }

        // Validaciones
        validadores.forEach(v -> v.validar(datosReservaConsulta)); // ejecutamos el metodo validar de cada validador

//        var médico = medicoRepository.findById(datosReservaConsulta.idMedico()).get(); // esto puede ser null mejor lo reemplazo por:
        var medico = elegirMedico(datosReservaConsulta);
        if (medico == null) {
            throw new ValidacionException("No existe un Médico disponible en ese horario");
        }
        var paciente = pacienteRepository.findById(datosReservaConsulta.idPaciente()).get();
        var consulta = new Consulta(null, medico, paciente, datosReservaConsulta.fecha(), null);

        consultaRepository.save(consulta);
        return new DatosDetalleConsulta(consulta);
    }

    private Medico elegirMedico(DatosReservaConsulta datosReservaConsulta) {
        if (datosReservaConsulta.idMedico() != null){
            return medicoRepository.getReferenceById(datosReservaConsulta.idMedico());
        }

        if (datosReservaConsulta.especialidad() == null){
            throw new ValidacionException("Debe elegir especialidad para reservar consulta");
        }

        return medicoRepository.elegirMedicoDisponibleEnLaFecha(datosReservaConsulta.especialidad(), datosReservaConsulta.fecha());

    }

    public void cancelar(DatosCancelarConsulta datosCancelarConsulta) {
        if (!consultaRepository.existsById(datosCancelarConsulta.idConsulta())){
            throw new ValidacionException("No existe un Consulta con el id informado");
        }
        var consulta = consultaRepository.getReferenceById(datosCancelarConsulta.idConsulta());
        consulta.cancelar(datosCancelarConsulta.motivoCancelacion());
    }
}
