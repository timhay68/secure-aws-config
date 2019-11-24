package au.com.haystacker.secureawsconfig.secrets.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AwsSecretsConfigurationTest {

    @Mock
    private AwsSecretProperties properties;

    @Mock
    private SecretsManagerClient client;

    private GetSecretValueResponse getSecretValueResponse;

    @InjectMocks
    private AwsSecretsConfiguration target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReadStringSecrets() {
        final GetSecretValueResponse getSecretValueResponse = GetSecretValueResponse.builder()
                .secretString("{\"mysql-username\":\"appuser\",\"mysql-password\":\"password123\"}")
                .build();

        when(client.getSecretValue(isA(GetSecretValueRequest.class))).thenReturn(getSecretValueResponse);

        target.getSecrets();

        final Map<String, String> appSecrets = target.appSecrets();
        assertEntry(appSecrets, "mysql-username", "appuser");
        assertEntry(appSecrets, "mysql-password", "password123");
    }

    @Test
    public void shouldReadBinarySecrets() {

        final String secretBinary = "eyJteXNxbC11c2VybmFtZSI6ImFwcHVzZXIiLCJteXNxbC1wYXNzd29yZCI6InBhc3N3b3JkMTIzIn0=";
        final SdkBytes secretBinaryByteBuffer = SdkBytes.fromByteArray(secretBinary.getBytes());

        final GetSecretValueResponse getSecretValueResponse = GetSecretValueResponse.builder()
                .secretString(null)
                .secretBinary(secretBinaryByteBuffer)
                .build();

        when(client.getSecretValue(isA(GetSecretValueRequest.class))).thenReturn(getSecretValueResponse);

        target.getSecrets();

        final Map<String, String> appSecrets = target.appSecrets();
        assertEntry(appSecrets, "mysql-username", "appuser");
        assertEntry(appSecrets, "mysql-password", "password123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenSecretsAreNotAMap() {

        final GetSecretValueResponse getSecretValueResponse = GetSecretValueResponse.builder()
                .secretString("{\"key-with-no-value\":,\"mysql-password\":\"password123\"}")
                .build();

        when(client.getSecretValue(isA(GetSecretValueRequest.class))).thenReturn(getSecretValueResponse);

        target.getSecrets();

        final Map<String, String> appSecrets = target.appSecrets();
    }

    private void assertEntry(Map<String, String> appSecrets, String expectedKey, String expectedValue) {
        final String actualValue = appSecrets.get(expectedKey);
        assertEquals(expectedValue, actualValue);
    }
}
