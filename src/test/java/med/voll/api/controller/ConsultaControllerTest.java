package med.voll.api.controller;

import med.voll.api.domain.consulta.DatosDetalleConsulta;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.consulta.ReservaDeConsultas;
import med.voll.api.domain.medico.Especialidad;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest // Nos sube el contexto para el acceso a un Controller
@AutoConfigureMockMvc // Para poder utilizar el MockMvc
@AutoConfigureJsonTesters // Para poder usar el JacksonTester
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc; // llamado al controller y hacer la consulta -> test de Unidad

    // Usamos una clase que se ocupa de armar el JSON Automáticamente
    @Autowired
    private JacksonTester<DatosReservaConsulta> datosReservaConsultaJson; // arma el JSON

    @Autowired
    private JacksonTester<DatosDetalleConsulta> datosDetalleConsultaJson; // arma el JSON

    @MockBean
    private ReservaDeConsultas reservaDeConsultas; // Para hacer que reservaDeConsultas no pase por todos los métodos del Repository, no haga la consulta a la BD, sino que lo simulamos.

    @Test
    @DisplayName("Deberia devolver http 400 cuando la request no tenga datos")
    @WithMockUser
        // simular que estamos logueados
    void reservar_escenario1() throws Exception {
        var response = mockMvc.perform(post("/consultas")) // con mockmvc.perform -> llama a los métodos http hace el ataque al endpoint
                .andReturn().getResponse(); // obtenemos la respuesta que la request hace

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()); // comprueba si la respuesta (Status) es igual al error 400
    }

    @Test
    @DisplayName("Deberia devolver http 200 cuando la request reciba un json válido")
    // vamos a recibir un JSON y a devolver un JSON
    @WithMockUser
    void reservar_escenario2() throws Exception {

        // Variables para crear el new DatosReservaConsulta
        var fecha = LocalDateTime.now().plusHours(1); // con 1 hora más de la actual
        var especialidad = Especialidad.CARDIOLOGIA;
        // para usarlo como el JSON que debe devolver
        var datosDetalleConsulta = new DatosDetalleConsulta(null, 2l, 5l, fecha);

        // Simulamos que la consulta a la BD nos devuelve un JSON
        when(reservaDeConsultas.reservar(any())).thenReturn(datosDetalleConsulta); //cuando se ingrese al metodo reservar() entonces devuelva el JSON datosDetalleConsulta

        var response = mockMvc.perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON) // decimos que vamos a enviar un JSON
                        .content(datosReservaConsultaJson.write( // ponemos el JSON armado con el JacksonTEster en content("{JSON}") se pone un objeto y se convierte a Json con datosReservaConsultaJson.write donde write da un objeto tipo JsonContent
                                        new DatosReservaConsulta(2l, 5l, fecha, especialidad) // objeto que queremos poner en el JSON
                                ).getJson() // permite obtener el JSON a partir del JsonContent logrado con el write
                        )
                )
                .andReturn().getResponse(); // nos da el Response y lo guardamos en la variable response

        // nuestro sistema debería pasar por el Controller y devolvernos un JSON
        var jsonEsperado = datosDetalleConsultaJson.write(
                datosDetalleConsulta
        ).getJson(); // permite obtener el JSON a partir del JsonContent logrado con el write

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // Comparamos si nos da un OK (200)
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado); // Afirmar si son iguales la respuesta y el JSON esperado (del response obtenemos el contenido con response.getContentAsString()
    }
}