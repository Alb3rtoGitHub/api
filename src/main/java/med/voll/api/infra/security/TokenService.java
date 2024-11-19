package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import med.voll.api.domain.usuarios.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String apiSecret; // toma el valor de application.properties

    public String generarToken(Usuario usuario) {// agregar el parámetro Usuario usuario
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret); // <-mala práctica "123456" cambiar por
            return JWT.create()
                    .withIssuer("voll med")
                    .withSubject(usuario.getLogin()) // <- mejorar cambiar "diego.rojas@voll.med" x usuario.getLogin()
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("error al generar el  token jwt", exception);
        }
    }

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // expira en 2Hs con offset hora de Sudamérica
    }
}
