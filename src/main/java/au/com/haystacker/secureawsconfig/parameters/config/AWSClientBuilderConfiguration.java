package au.com.haystacker.secureawsconfig.parameters.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 *
 *
 * @author Tim Hay
 */
@Configuration
public class AWSClientBuilderConfiguration {

    @Autowired
    private AwsParameterStoreProperties properties;

    @Autowired
    AwsCredentialsProvider awsCredentialsProvider;

    @Bean
    public SsmClient simpleSystemsManagementClient() {
        SsmClient client = SsmClient.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(awsCredentialsProvider)
                .build();

        return client;
    }
}
