package org.biocode.bcid;


import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"org.biocode.bcid"})
@PropertySource("classpath:bcid.properties")
@Import({DataAccessConfig.class})
public class BcidAppConfig {
}
