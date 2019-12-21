package au.com.haystacker.secureawsconfig.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;

/**
 *
 *
 * @author Tim Hay
 */
@Configuration
public class AWSSecureConfigConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public AwsCredentialsProvider defaultAWSCredentialsProvider() {
        return InstanceProfileCredentialsProvider.create();
    }
}
