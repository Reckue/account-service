package com.reckue.account.configs;

import com.fasterxml.classmate.TypeResolver;
import com.reckue.account.transfers.ErrorTransfer;
import org.hibernate.mapping.Any;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Class SwaggerConfig sets the settings for swagger.
 *
 * @author Kamila Meshcheryakova
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    /**
     * Redirects users from home page to the Swagger UI page.
     *
     * @param registry assists with the registration of simple automated controllers pre-configured
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    /**
     * This method creates a customised Docket bean.
     *
     * @return instance of the implementation of the interface Docket
     */
    @Bean
    public Docket docket(TypeResolver typeResolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.reckue.account.controllers"))
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(apiKey()))
                .useDefaultResponseMessages(false)
                .additionalModels(typeResolver.resolve(ErrorTransfer.class))
                .globalResponseMessage(RequestMethod.POST, newArrayList(new ResponseMessageBuilder().code(201)
                                .message("CREATED")
                                .responseModel(new ModelRef("AuthTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(400)
                                .message("BAD_REQUEST")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(401)
                                .message("UNAUTHORIZED")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(403)
                                .message("ACCESS_DENIED")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(409)
                                .message("CONFLICT")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(500)
                                .message("INTERNAL_SERVER_ERROR")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build()))
                .useDefaultResponseMessages(false)
                .additionalModels(typeResolver.resolve(ErrorTransfer.class))
                .globalResponseMessage(RequestMethod.GET, newArrayList(
                        new ResponseMessageBuilder().code(400)
                                .message("BAD_REQUEST")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(401)
                                .message("UNAUTHORIZED")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(403)
                                .message("ACCESS_DENIED")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(404)
                                .message("NOT_FOUND")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(500)
                                .message("INTERNAL_SERVER_ERROR")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build()))
                .useDefaultResponseMessages(false)
                .additionalModels(typeResolver.resolve(ErrorTransfer.class))
                .globalResponseMessage(RequestMethod.DELETE, newArrayList(new ResponseMessageBuilder().code(400)
                                .message("BAD_REQUEST")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(403)
                                .message("ACCESS_DENIED")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(404)
                                .message("NOT_FOUND")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build(),
                        new ResponseMessageBuilder().code(500)
                                .message("INTERNAL_SERVER_ERROR")
                                .responseModel(new ModelRef("ErrorTransfer"))
                                .build()));
    }

    /**
     * This method sets up tittle and description for swagger.
     *
     * @return page swagger-ui.html with custom fields
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Account API")
                .description("Provide authorization and authentication")
                .contact(new Contact("Reckue", "www.reckue.com", "support@reckue.com"))
                .build();
    }

    /**
     * This method allows to add authorize button to swagger configuration.
     *
     * @return apiKey with given parameters
     */
    private ApiKey apiKey() {
        return new ApiKey("Bearer token", "Authorization", "header");
    }
}
