package org.quarkus.irccs.authentication.model;

import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtPayload {
    public String iss;
    public String sub;
    public Long iat;
    public Long exp;
    public List<String> groups;
    public String jti;
}
