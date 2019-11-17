package au.com.haystacker.secureawsconfig.parameters.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSClientBuilderConfiguration {

    @Autowired
    private AwsParameterStoreProperties properties;

    @Autowired
    AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public AWSSimpleSystemsManagement simpleSystemsManagementClient() {
        AWSSimpleSystemsManagement client  = AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(properties.getRegion())
                .withCredentials(awsCredentialsProvider)
                .build();

        return client;
    }
}
