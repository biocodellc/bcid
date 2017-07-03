package org.biocode.bcid;


import org.biocode.bcid.ezid.EzidService;
import org.biocode.bcid.rest.ClientContext;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"org.biocode.bcid"})
@PropertySource("classpath:bcid.properties")
@Import({DataAccessConfig.class})
public class BcidAppConfig {

    @Bean
    @Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ClientContext clientContext() {
        return new ClientContext();
    }

    @Bean
    public EzidService ezidService() {
        return new EzidService();
    }
}
