package io.github.vzwingma.finances.budget.services.communs.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter @Setter
public class JWTIdToken {

    private static final Logger LOG = LoggerFactory.getLogger(JWTIdToken.class);
    private JWTHeader header;

    private JWTPayload payload;

    public JWTIdToken(JWTHeader header, JWTPayload payload){
        this.header = header;
        this.payload = payload;
    }

    /**
     * Header d'un token JWT
     */
    @Setter @Getter @NoArgsConstructor
    public static class JWTHeader {
        private String alg;
        private String kid;
        private String typ;
    }

    @Setter @Getter @NoArgsConstructor
    public static class JWTPayload {
        private String iss;
        private String azp;
        private String aud;
        private String sub;
        private String email;
        private boolean email_verified;
        private String at_hash;
        private String name;
        private String picture;
        private String given_name;
        private String family_name;
        private String locale;
        private long iat;
        private long exp;

        @Override
        public String toString() {
            return "JWTPayload{" +
                    "name='" + name + '\'' +
                    ", iat=" + iat +
                    ", exp=" + exp +
                    '}';
        }
    }

    @JsonIgnore
    public LocalDateTime issuedAt(){
        if(this.payload != null && this.payload.iat != 0){
            return LocalDateTime.ofEpochSecond(this.getPayload().getIat(), 0, ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now()));
        }
        return null;
    }
    @JsonIgnore
    public LocalDateTime expiredAt(){
        if(this.payload != null && this.payload.exp != 0){
            return LocalDateTime.ofEpochSecond(this.getPayload().getExp(),0, ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now()));
        }
        return null;
    }

    /**
     *
     * @return l'expiration
     */
    public boolean isExpired(){
        LocalDateTime expAt = expiredAt();
        if(expAt != null){
            return !LocalDateTime.now().isBefore(expAt);
        }
        return false;
    }

    @Override
    public String toString() {
        return "JWTIdToken{" +
                ", payload=" + payload +
                ", isExpired=" + isExpired() +
                '}';
    }
}
