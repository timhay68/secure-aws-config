package au.com.haystacker.secureawsconfig.parameters.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.utils.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 *
 * @author Tim Hay
 */
@Configuration
@EnableConfigurationProperties(AwsParameterStoreProperties.class)
public class AwsParameterStoreConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AwsParameterStoreConfiguration.class);

    @Autowired
    private AwsParameterStoreProperties properties;

    @Autowired
    private SsmClient client;

    private Map<String, String> awsParameters;

    @PostConstruct
    public void getParameters() {

        LOG.debug("AwsParameterStoreConfiguration.getParameters()");

        GetParametersByPathRequest request = GetParametersByPathRequest.builder()
                .recursive(true)
                .withDecryption(true)
                .path(properties.getPathBase())
                .build();
        GetParametersByPathResponse result = null;

        result = client.getParametersByPath(request);

        awsParameters = result.parameters().stream()
                .collect(Collectors.toMap(
                        parameter -> extractParameterName(parameter.name()),
                        parameter -> parameter.value()
                        )
                );
    }

    /**
     * This allows us to specify in the annotation just the path to
     * the parameter value, without the base path.
     * eg: for the parameter with full path {@code /myapi/dev/db/password},
     * we could have the base path as {@code /myapi/dev} and the annotation
     * as {@code AwsParameter(name = "/db/password")}
     *
     * @param paramPath The full path to the parameter stored in AWS Systems Manager Parameter Store.
     * @return The name of the parameter without the base path prefix
     */
    private String extractParameterName(final String paramPath) {
        return StringUtils.replacePrefixIgnoreCase(paramPath, properties.getPathBase(), "");
    }

    @Bean(name = "awsParameters")
    public Map<String, String> appSecrets() {
        return awsParameters;
    }
}
