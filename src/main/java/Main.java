import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=FEFLEzZw9wOxtTtL2JMPvfm1DFdLWUOgbm8K3d5R";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();


        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);
        CloseableHttpResponse response = httpClient.execute(request);

        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        AnObject anObject = mapper.readValue(body, AnObject.class);
        int index = anObject.getUrl().lastIndexOf('/');
        String name = anObject.getUrl().substring(index+1);

        response = httpClient.execute(new HttpGet(anObject.getUrl()));
        byte[] picture = response.getEntity().getContent().readAllBytes();
        try (FileOutputStream fos = new FileOutputStream(name)){
            fos.write(picture, 0, picture.length);
        }
    }
}
