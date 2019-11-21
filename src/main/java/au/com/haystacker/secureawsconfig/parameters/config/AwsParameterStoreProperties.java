package au.com.haystacker.secureawsconfig.parameters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 *
 * @author Tim Hay
 */
@ConfigurationProperties(prefix = AwsParameterStoreProperties.PREFIX)
public class AwsParameterStoreProperties {

    static final String PREFIX = "secure-aws-config.parameters";

    private String pathBase;
    private String region;

    public AwsParameterStoreProperties() {
    }

    public String getPathBase() {
        return pathBase;
    }

    public void setPathBase(String pathBase) {
        this.pathBase = pathBase;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
