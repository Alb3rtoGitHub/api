package med.voll.api.controller;

import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.direccion.Direccion;
import med.voll.api.domain.medico.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosRegistroMedico> datosRegistroMedicoJson;

    @Autowired
    private JacksonTester<DatosDetalleMedico> datosDetalleMedicoJson;

    @MockBean
    private MedicoRepository medicoRepository;

    @Test
    @DisplayName("Debería devolver código http 400 cuando las informaciones son inválidas")
    @WithMockUser
    void registrarMedico_escenario1() throws Exception {
        var response = mockMvc.perform(post("/medicos"))
                .andReturn().getResponse();
        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver código http 200 cuando las informaciones son válidas")
    @WithMockUser
    void registrarMedico_escenario2() throws Exception {
        var datosRegistroMedico = new DatosRegistroMedico(
                "Medico1",
                "medico1@voll.med",
                "6199999999",
                "965874",
                Especialidad.CARDIOLOGIA,
                datosDireccion()
        );
        when(medicoRepository.save(any())).thenReturn(new Medico(datosRegistroMedico));

        var response = mockMvc.perform(post("/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosRegistroMedicoJson.write(datosRegistroMedico).getJson()))
                .andReturn().getResponse();

        var datosDetalleMedico = new DatosDetalleMedico(
                null,
                datosRegistroMedico.nombre(),
                datosRegistroMedico.email(),
                datosRegistroMedico.documento(),
                datosRegistroMedico.telefono(),
                datosRegistroMedico.especialidad(),
                datosRegistroMedico.direccion()
//                datosRegistroMedico.direccion()
        );
        var jsonEsperado = datosDetalleMedicoJson.write(datosDetalleMedico).getJson();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    private DatosDireccion datosDireccion() {
        return new DatosDireccion(
                "calle ejemplo",
                "distrito 14",
                "Buenos Aires",
                "789",
                "99"
        );
    }
}