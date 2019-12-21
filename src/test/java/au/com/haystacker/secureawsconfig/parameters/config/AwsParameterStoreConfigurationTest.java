package au.com.haystacker.secureawsconfig.parameters.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AwsParameterStoreConfigurationTest {

    public static final String PATH_BASE = "/myapi/test";

    @Mock
    private AwsParameterStoreProperties properties;

    @Mock
    private SsmClient client;

    private GetParametersByPathResponse result;

    @InjectMocks
    private AwsParameterStoreConfiguration target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldExtractSecretsWithBasePath() {

        final List<Parameter> params = new ArrayList<>();
        params.add(buildParameter("db/username", "value1"));
        params.add(buildParameter("db/password", "value2"));
        params.add(buildParameter("some/other/config", "value3"));

        final GetParametersByPathResponse result = GetParametersByPathResponse.builder()
                .parameters(params)
                .build();

        when(properties.getPathBase()).thenReturn(PATH_BASE);
        when(client.getParametersByPath(isA(GetParametersByPathRequest.class))).thenReturn(result);

        target.getParameters();

        final Map<String, String> appSecrets = target.appSecrets();
        assertEntry(appSecrets, "/db/username", "value1");
        assertEntry(appSecrets, "/db/password", "value2");
        assertEntry(appSecrets, "/some/other/config", "value3");
    }

    @Test
    public void shouldExtractNoSecretsWithBasePathHavingNoEntries() {

        final List<Parameter> params = new ArrayList<>();
        final GetParametersByPathResponse result = GetParametersByPathResponse.builder()
                .parameters(params)
                .build();

        when(properties.getPathBase()).thenReturn(PATH_BASE);
        when(client.getParametersByPath(isA(GetParametersByPathRequest.class))).thenReturn(result);

        target.getParameters();

        final Map<String, String> appSecrets = target.appSecrets();
        assertTrue(appSecrets.isEmpty());
    }

    private void assertEntry(Map<String, String> appSecrets, String expectedKey, String expectedValue) {
        final String actualValue = appSecrets.get(expectedKey);
        assertEquals(expectedValue, actualValue);
    }

    private static Parameter buildParameter(String key, String value) {
        final Parameter param = Parameter.builder()
                .name(PATH_BASE + "/" + key)
                .value(value)
                .build();

        return param;
    }
}
