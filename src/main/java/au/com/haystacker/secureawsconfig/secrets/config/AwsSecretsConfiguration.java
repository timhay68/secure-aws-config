package au.com.haystacker.secureawsconfig.secrets.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;

/**
 *
 *
 * @author Tim Hay
 */
@Configuration
@EnableConfigurationProperties(AwsSecretProperties.class)
public class AwsSecretsConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AwsSecretsConfiguration.class);

    @Autowired
    private AwsSecretProperties properties;

    @Autowired
    private AWSSecretsManager client;

    private Map<String, String> awsSecrets;

    @PostConstruct
    public void getSecrets() {

        LOG.info("**********   AwsSecretsConfiguration.getSecrets()");

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(properties.getSecretName());
        GetSecretValueResult getSecretValueResult = null;

        getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        String secrets;
        if (getSecretValueResult.getSecretString() != null) {
            secrets = getSecretValueResult.getSecretString();
        } else {
            final ByteBuffer secretBinary = getSecretValueResult.getSecretBinary();
            final ByteBuffer decodedSecretBinary = Base64.getDecoder().decode(secretBinary);
            secrets = new String(decodedSecretBinary.array());
        }
        final ObjectMapper mapper = new ObjectMapper();
        try {
            awsSecrets = mapper.readValue(secrets, new TypeReference<Map<String, String>>() {});
        } catch (IOException exception) {
            String msg = String.format("Failed to deserialise secrets from AWS Managed Secrets");
            throw new IllegalArgumentException(msg, exception);
        }
    }

    @Bean(name = "awsSecrets")
    public Map<String, String> appSecrets() {
        return awsSecrets;
    }
}
