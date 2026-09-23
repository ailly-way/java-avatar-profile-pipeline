import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class InfraiClient {
    private final HttpClient http = HttpClient.newHttpClient();
    private final String key;
    private final String baseUrl;

    public InfraiClient(String key, String baseUrl) {
        this.key = key;
        this.baseUrl = baseUrl;
    }

    public String post(String path, String json) throws IOException, InterruptedException {
        return send("POST", path, json);
    }

    public String patch(String path, String json) throws IOException, InterruptedException {
        return send("PATCH", path, json);
    }

    private String send(String method, String path, String json) throws IOException, InterruptedException {
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (response.statusCode() == 429 && attempt < 2) {
                Thread.sleep((long) Math.pow(2, attempt) * 500L);
                continue;
            }
            boolean ok = body.contains("\"ok\":true");
            if (!ok) {
                throw new InfraiException("Infrai rejected request: " + body, response.statusCode());
            }
            if (response.statusCode() >= 500) {
                throw new InfraiException("Infrai transport failure: HTTP " + response.statusCode(), response.statusCode());
            }
            if (path.startsWith("/v1/image/")) {
                String field = path.equals("/v1/image/smart_crop") ? "url" : "image_id";
                java.util.regex.Matcher image = java.util.regex.Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
                if (!image.find()) throw new InfraiException("Infrai image response has no image value: " + body, response.statusCode());
                return image.group(1);
            }
            return body;
        }
        throw new InfraiException("Request retry budget exhausted", 429);
    }

    public static final class InfraiException extends IOException {
        public final int status;
        public InfraiException(String message, int status) { super(message); this.status = status; }
    }
}
