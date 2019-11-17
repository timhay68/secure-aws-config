package au.com.haystacker.secureawsconfig.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSSecureConfigConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public AWSCredentialsProvider defaultAWSCredentialsProvider() {
        return InstanceProfileCredentialsProvider.getInstance();
    }
}
