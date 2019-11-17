package au.com.haystacker.secureawsconfig.secrets.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = AwsSecretProperties.PREFIX)
public class AwsSecretProperties {

    static final String PREFIX = "secure-aws-config.secrets";

    private String secretName;
    private String region;

    public AwsSecretProperties() {
    }

    public String getSecretName() {
        return secretName;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
