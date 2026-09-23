import java.io.IOException;

public final class AvatarPipelineService {
    private final InfraiClient infrai;

    public AvatarPipelineService(InfraiClient infrai) { this.infrai = infrai; }

    public String process(String userId, String filename, String imageBase64) throws IOException, InterruptedException {
        String uploaded = infrai.post("/v1/image/upload", "{\"file\":\"" + imageBase64 + "\",\"filename\":\"" + filename + "\"}");
        String cropped = infrai.post("/v1/image/smart_crop", "{\"image\":{\"image_id\":\"" + uploaded + "\"},\"aspect\":\"1:1\"}");
        String optimized = infrai.post("/v1/image/resize", "{\"image\":{\"base64\":\"" + cropped.substring(cropped.indexOf(',') + 1) + "\"},\"width\":512,\"height\":512,\"fit\":\"cover\",\"enlarge\":false,\"format\":\"webp\",\"store\":true}");
        infrai.patch("/v1/auth/user/update/" + userId, "{\"user_id\":\"" + userId + "\",\"metadata\":{\"image\":\"" + optimized + "\"}}");
        return optimized;
    }
}
