package au.com.haystacker.secureawsconfig.secrets.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSClientBuilderConfiguration {

    @Autowired
    private AwsSecretProperties properties;

    @Autowired
    AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public AWSSecretsManager secretsManagerClient() {

        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(properties.getRegion())
                .withCredentials(awsCredentialsProvider)
                .build();

        return client;
    }
}
