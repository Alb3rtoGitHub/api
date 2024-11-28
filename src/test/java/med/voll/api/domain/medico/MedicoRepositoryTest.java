package med.voll.api.domain.medico;

import jakarta.persistence.EntityManager;
import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.paciente.DatosRegistroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // para probar los metodos qus usan JPA
// En el caso de usar una BD en memoria como H2 sera necesario agregar esa dependencia y sacar @AutoConfigureTestDatabase, @ActiveProfiles y el archivo application-test.properties
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// para usar la misma BD que usamos en nuestro sistema -> Replace.NONE
@ActiveProfiles("test") // para indicarle el contexto que debe usar en application-<contexto>.properties -> test y alli debe esta la BD de test similar a nuestra BD original
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository; // nos permite acceder a la BD

    @Autowired
    private EntityManager em; // me permite acceder a los repositories que usen mis variables asi no importo uno por uno

    @Test
    @DisplayName("Debería devolver null cuando el medico buscado existe pero no esta disponible en esa fecha") // esta anotación permite colocar un nombre aclarando de que trata este test
    void elegirMedicoDisponibleEnLaFechaEscenario1() {
        // given o arrange
        var lunesSiguienteALas10 = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10, 0); // variable fecha que contemple hoy + un tiempo de ajuste
        // Vamos a llenar nuestra BD con el escenario a probar: un médico, un paciente y una consulta en el horario que quiero testear indisponibilidad
        var medico = registrarMedico("Medico1", "medico@gmail.com", "123456", Especialidad.CARDIOLOGIA);
        var paciente = registrarPaciente("Paciente1", "paciente@gmail.com", "243456");
        registrarConsulta(medico, paciente, lunesSiguienteALas10);
        // when o act -> ejecuto la consulta con misma especialidad y fecha para luego comparar con assertThat
        var medicoLibre = medicoRepository.elegirMedicoDisponibleEnLaFecha(Especialidad.CARDIOLOGIA, lunesSiguienteALas10); // variable fecha que no quede vieja para hacer pruebas en el futuro
        // then o assert
        assertThat(medicoLibre).isNull(); //probar que el médico estaba ocupado en esa fecha
    }

    @Test
    @DisplayName("Debería devolver medico cuando el medico buscado esta disponible en esa fecha")
    void elegirMedicoDisponibleEnLaFechaEscenario2() {
        // given o arrange
        var lunesSiguienteALas10 = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10, 0);
        var medico = registrarMedico("Medico1", "medico@gmail.com", "123456", Especialidad.CARDIOLOGIA);
        // when o act
        var medicoLibre = medicoRepository.elegirMedicoDisponibleEnLaFecha(Especialidad.CARDIOLOGIA, lunesSiguienteALas10);
        // then o assert
        assertThat(medicoLibre).isEqualTo(medico);
    }

    // Métodos privados necesarios para varios tests
    private void registrarConsulta(Medico medico, Paciente paciente, LocalDateTime fecha) {
        em.persist(new Consulta(null, medico, paciente, fecha, null));
    }

    private Medico registrarMedico(String nombre, String email, String documento, Especialidad especialidad) {
        var medico = new Medico(datosMedico(nombre, email, documento, especialidad));
        em.persist(medico);
        return medico;
    }

    private Paciente registrarPaciente(String nombre, String email, String documento) {
        var paciente = new Paciente(datosPaciente(nombre, email, documento));
        em.persist(paciente);
        return paciente;
    }

    private DatosRegistroMedico datosMedico(String nombre, String email, String documento, Especialidad especialidad) {
        return new DatosRegistroMedico(
                nombre,
                email,
                "6145489789",
                documento,
                especialidad,
                datosDireccion()
        );
    }

    private DatosRegistroPaciente datosPaciente(String nombre, String email, String documento) {
        return new DatosRegistroPaciente(
                nombre,
                email,
                "1234564",
                documento,
                datosDireccion()
        );
    }

    private DatosDireccion datosDireccion() {
        return new DatosDireccion(
                "calle x",
                "distrito y",
                "ciudad z",
                "123",
                "1"
        );
    }
}