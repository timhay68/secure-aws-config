package au.com.haystacker.secureawsconfig.parameters.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(AwsParameterStoreProperties.class)
public class AwsParameterStoreConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AwsParameterStoreConfiguration.class);

    @Autowired
    private AwsParameterStoreProperties properties;

    @Autowired
    private AWSSimpleSystemsManagement client;

    private Map<String, String> awsParameters;

    @PostConstruct
    public void getParameters() {

        LOG.debug("AwsParameterStoreConfiguration.getParameters()");

        GetParametersByPathRequest request = new GetParametersByPathRequest()
                .withRecursive(true)
                .withPath(properties.getPathBase());
        GetParametersByPathResult result = null;

        result = client.getParametersByPath(request);

        awsParameters = result.getParameters().stream()
                .collect(Collectors.toMap(
                        parameter -> extractParameterName(parameter.getName()),
                        parameter -> parameter.getValue()
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
        return StringUtils.replace(paramPath, properties.getPathBase(), "");
    }

    @Bean(name = "awsParameters")
    public Map<String, String> appSecrets() {
        return awsParameters;
    }
}
