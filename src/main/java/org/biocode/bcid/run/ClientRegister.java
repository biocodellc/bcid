package org.biocode.bcid.run;

import org.biocode.bcid.BcidAppConfig;
import org.biocode.bcid.models.ClientCredentials;
import org.biocode.bcid.service.ClientService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author rjewing
 */
public class ClientRegister {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BcidAppConfig.class);
        ClientService clientService = applicationContext.getBean(ClientService.class);

        ClientCredentials creds = clientService.create();

        System.out.println("\n\nClient ID: " + creds.id + "\nSecret: " + creds.secret);
    }
}
