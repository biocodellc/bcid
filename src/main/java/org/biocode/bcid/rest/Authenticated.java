package org.biocode.bcid.rest;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation for checking if a client is authenticated
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {}
