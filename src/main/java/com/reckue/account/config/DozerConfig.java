package com.reckue.account.config;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class DozerConfig sets the settings for dozer.
 *
 * @author Kamila Meshcheryakova
 */
@Configuration
public class DozerConfig {

    /**
     * This method is used to perform several one-time initializations and loads the custom xml mappings.
     *
     * @return an object of DozerBeanMapper class
     */
    @Bean
    public DozerBeanMapper getDozerBeanMapper() {
        return new DozerBeanMapper();
    }
}

