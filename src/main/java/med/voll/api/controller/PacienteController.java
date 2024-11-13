package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.paciente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping
//    @Transactional
    public void registrarPaciente(@RequestBody @Valid DatosRegistroPaciente datosRegistroPaciente) {
        pacienteRepository.save(new Paciente(datosRegistroPaciente));
    }

    @GetMapping
    public Page<DatosListadoPaciente> listadoPacientes(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion) {
//    public Page<DatosListadoPaciente> listadoPacientes(Pageable paginacion) {

        // Muestra todos los pacientes
//        return pacienteRepository.findAll(paginacion)
//                .map(DatosListadoPaciente::new);

        // Muestra solo los pacientes activos
        return pacienteRepository.findByActivoTrue(paginacion)
                .map(DatosListadoPaciente::new);
    }

    @PutMapping
    @Transactional
    public void actualizarPaciente(@RequestBody @Valid DatosActualizarPaciente datosActualizarPaciente) {
        Paciente paciente = pacienteRepository.getReferenceById(datosActualizarPaciente.id());
        paciente.actualizarDatos(datosActualizarPaciente);
    }

    @DeleteMapping("/{id}")
    @Transactional
    // Para DELETE lógico:
    public void eliminarPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        paciente.desactivarPaciente();
    }

    // Para DELETE de BD:
//    public void eliminarPaciente(@PathVariable Long id) {
//        Paciente paciente = pacienteRepository.getReferenceById(id);
//        pacienteRepository.delete(paciente);
//    }
}
