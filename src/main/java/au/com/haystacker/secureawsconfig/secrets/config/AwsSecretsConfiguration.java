package au.com.haystacker.secureawsconfig.secrets.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

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
    private SecretsManagerClient client;

    private Map<String, String> awsSecrets;

    @PostConstruct
    public void getSecrets() {

        LOG.info("**********   AwsSecretsConfiguration.getSecrets()");

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(properties.getSecretName())
                .build();
        GetSecretValueResponse getSecretValueResult = null;

        getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        String secrets;
        if (getSecretValueResult.secretString() != null) {
            secrets = getSecretValueResult.secretString();
        } else {
            final SdkBytes secretBinary = getSecretValueResult.secretBinary();
            final ByteBuffer decodedSecretBinary = Base64.getDecoder().decode(secretBinary.asByteBuffer());
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
