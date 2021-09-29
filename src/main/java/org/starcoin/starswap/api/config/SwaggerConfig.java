package org.starcoin.starswap.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
//@Profile("dev")
public class SwaggerConfig {

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo()).select()
                .paths(PathSelectors.regex("/error.*").negate())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Starswap API").description("Starswap API").version("1.0.0").build();
    }
}
