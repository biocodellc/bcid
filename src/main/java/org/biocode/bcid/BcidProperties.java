package org.biocode.bcid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author rjewing
 */
@Component
public class BcidProperties {

    private final Environment env;

    @Autowired
    public BcidProperties(Environment env) {
        this.env = env;
    }

    public String mailUser() {
        return env.getRequiredProperty("mailUser");
    }

    public String mailPassword() {
        return env.getRequiredProperty("mailPassword");
    }

    public String mailFrom() {
        return env.getRequiredProperty("mailFrom");
    }

    public int naan() {
        return env.getRequiredProperty("naan", int.class);
    }

    public String ezidUser() {
        return env.getRequiredProperty("ezidUser");
    }

    public String ezidPass() {
        return env.getRequiredProperty("ezidPass");
    }

    public boolean ezidRequest() {
        return env.getRequiredProperty("ezidRequests", boolean.class);
    }
}
