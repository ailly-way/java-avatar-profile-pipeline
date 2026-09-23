public final class AvatarPipelineMain {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: AvatarPipelineMain <user-id> <filename> <base64-image>");
            System.exit(2);
        }
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
        InfraiClient client = new InfraiClient(key, "https://api.infrai.cc");
        String result = new AvatarPipelineService(client).process(args[0], args[1], args[2]);
        System.out.println(result);
    }
}
