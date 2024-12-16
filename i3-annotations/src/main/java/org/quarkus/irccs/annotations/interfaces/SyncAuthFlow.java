package org.quarkus.irccs.annotations.interfaces;

import jakarta.annotation.Priority;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Target({ElementType.LOCAL_VARIABLE, METHOD, TYPE})
@Retention(RUNTIME)
@Priority(0)
public @interface SyncAuthFlow {}