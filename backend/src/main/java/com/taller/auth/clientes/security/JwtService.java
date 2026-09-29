package com.taller.auth.clientes.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
@Service
public class JwtService {
    private static final Base64.Encoder E=Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder D=Base64.getUrlDecoder();
    private final ObjectMapper json; private final String secret;
    public JwtService(ObjectMapper json,@Value("${JWT_SECRET:${app.jwt.secret:}}") String secret){this.json=json;this.secret=secret;}
    public String issue(long id,String usuario,String rol){
        if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalStateException("JWT_SECRET requiere 32 bytes.");
        try{long now=Instant.now().getEpochSecond();String h=E.encodeToString(json.writeValueAsBytes(Map.of("alg","HS256","typ","JWT")));String p=E.encodeToString(json.writeValueAsBytes(Map.of("sub",id,"usuario",usuario,"rol",rol.toUpperCase(),"exp",now+28800)));String body=h+"."+p;return body+"."+E.encodeToString(sign(body));}catch(Exception e){throw new IllegalStateException(e);}
    }
    public Claims verify(String token){
        if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalStateException("JWT_SECRET requiere 32 bytes.");
        try{String[] p=token.split("\\.");if(p.length!=3)throw new IllegalArgumentException();String body=p[0]+"."+p[1];if(!MessageDigest.isEqual(sign(body),D.decode(p[2])))throw new IllegalArgumentException();var h=json.readTree(D.decode(p[0]));var c=json.readTree(D.decode(p[1]));if(!"HS256".equals(h.path("alg").asText())||c.path("exp").asLong()<=Instant.now().getEpochSecond())throw new IllegalArgumentException();return new Claims(c.path("sub").asText(),c.path("usuario").asText(),c.path("rol").asText());}catch(Exception e){throw new IllegalArgumentException("JWT invalido o vencido.",e);}
    }
    private byte[] sign(String value)throws Exception{Mac m=Mac.getInstance("HmacSHA256");m.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));return m.doFinal(value.getBytes(StandardCharsets.UTF_8));}
    public record Claims(String subject,String usuario,String rol){}
}
