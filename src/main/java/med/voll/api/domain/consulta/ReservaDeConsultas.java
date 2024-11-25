package med.voll.api.domain.consulta;

import med.voll.api.domain.ValidacionException;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaDeConsultas {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;


    public void reservar(DatosReservaConsulta datosReservaConsulta){

        if(!pacienteRepository.existsById(datosReservaConsulta.idPaciente())){
            throw new ValidacionException("No existe un Paciente con el id informado");
        }

        if (datosReservaConsulta.idMedico() != null && !medicoRepository.existsById(datosReservaConsulta.idMedico())){
            throw new ValidacionException("No existe un Médico con el id informado");
        }

//        var medico = medicoRepository.findById(datosReservaConsulta.idMedico()).get(); // esto puede ser null mejor lo reemplazo por:
        var medico = elegirMedico(datosReservaConsulta);
        var paciente = pacienteRepository.findById(datosReservaConsulta.idPaciente()).get();
        var consulta = new Consulta(null, medico, paciente, datosReservaConsulta.fecha());

        consultaRepository.save(consulta);
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
}
