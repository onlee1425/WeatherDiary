package zb.weather.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("zb.weather"))
                .paths(PathSelectors.any()) //path 기준으로 swagger에 표시될 api를 정하는 부분
                //.paths(PathSelectors.none()) //노출 x
                //.paths(PathSelectors.ant("/read/**")) //특정 경로의 api만 표시 /read로 시작하는 패턴을 전부 표시
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("날씨 일기 프로젝트")
                .description("날씨 일기를 CRUD 할 수 있는 백엔드 API 입니다")
                .version("2.0")
                .build();
    }
}
