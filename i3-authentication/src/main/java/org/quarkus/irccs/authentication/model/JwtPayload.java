package org.quarkus.irccs.authentication.model;


import java.util.List;

public class JwtPayload {
    public String iss;
    public String sub;
    public Long iat;
    public Long exp;
    public List<String> groups;
    public String jti;
}
