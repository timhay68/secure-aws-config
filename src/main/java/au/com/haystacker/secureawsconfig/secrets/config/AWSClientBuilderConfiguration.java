package au.com.haystacker.secureawsconfig.secrets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 *
 *
 * @author Tim Hay
 */
@Configuration
public class AWSClientBuilderConfiguration {

    @Autowired
    private AwsSecretProperties properties;

    @Autowired
    AwsCredentialsProvider awsCredentialsProvider;

    @Bean
    public SecretsManagerClient secretsManagerClient() {

        SecretsManagerClient client  = SecretsManagerClient.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(awsCredentialsProvider)
                .build();

        return client;
    }
}
