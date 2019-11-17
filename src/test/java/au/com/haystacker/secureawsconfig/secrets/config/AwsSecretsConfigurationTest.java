package au.com.haystacker.secureawsconfig.secrets.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AwsSecretsConfigurationTest {

    @Mock
    private AwsSecretProperties properties;

    @Mock
    private AWSSecretsManager client;

    @Mock
    private GetSecretValueResult getSecretValueResult;

    @InjectMocks
    private AwsSecretsConfiguration target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReadStringSecrets() {

        when(client.getSecretValue(any())).thenReturn(getSecretValueResult);
        when(getSecretValueResult.getSecretString()).thenReturn("{\"mysql-username\":\"appuser\",\"mysql-password\":\"password123\"}");

        target.getSecrets();

        final Map<String, String> appSecrets = target.appSecrets();
        assertEntry(appSecrets, "mysql-username", "appuser");
        assertEntry(appSecrets, "mysql-password", "password123");
    }

    @Test
    public void shouldReadBinarySecrets() {

        when(client.getSecretValue(any())).thenReturn(getSecretValueResult);
        when(getSecretValueResult.getSecretString()).thenReturn(null);

        final String secretBinary = "eyJteXNxbC11c2VybmFtZSI6ImFwcHVzZXIiLCJteXNxbC1wYXNzd29yZCI6InBhc3N3b3JkMTIzIn0=";
        ByteBuffer secretBinaryByteBuffer = ByteBuffer.wrap(secretBinary.getBytes());
        when(getSecretValueResult.getSecretBinary()).thenReturn(secretBinaryByteBuffer);

        target.getSecrets();

        final Map<String, String> appSecrets = target.appSecrets();
        assertEntry(appSecrets, "mysql-username", "appuser");
        assertEntry(appSecrets, "mysql-password", "password123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenSecretsAreNotAMap() {

        when(client.getSecretValue(any())).thenReturn(getSecretValueResult);
        when(getSecretValueResult.getSecretString()).thenReturn("{\"key-with-no-value\":,\"mysql-password\":\"password123\"}");

        target.getSecrets();

        final Map<String, String> appSecrets = target.appSecrets();
    }

    private void assertEntry(Map<String, String> appSecrets, String expectedKey, String expectedValue) {
        final String actualValue = appSecrets.get(expectedKey);
        assertEquals(expectedValue, actualValue);
    }
}
