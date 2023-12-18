package org.quarkus.irccs.annotations.interfaces;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.*;

@Inherited
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface I3Role {
    //todo:: Enable this if you trigger a specific role
    //@Nonbinding RoleEnum value();
}

