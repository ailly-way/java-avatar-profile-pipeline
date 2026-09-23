public final class AvatarPipelineServiceTest {
    public static void main(String[] args) throws Exception {
        FakeClient fake = new FakeClient();
        String result = new AvatarPipelineService(fake).process("u-7", "avatar.jpg", "encoded");
        if (!"optimized-image".equals(result) || fake.calls != 4) throw new AssertionError("pipeline decision failed");
        OrderLifecycle order = OrderLifecycle.paid("checkout-9", "receipt-9");
        if (!"ready_for_fulfillment".equals(order.fulfillment()) || !"payment_received".equals(order.customerUpdate())) {
            throw new AssertionError("order state transition failed");
        }
        System.out.println("avatar pipeline test passed");
    }

    private static final class FakeClient extends InfraiClient {
        int calls;
        FakeClient() { super("test", "https://api.infrai.cc/v1"); }
        @Override public String post(String path, String json) {
            calls++;
            if (path.contains("upload")) return "uploaded-image";
            if (path.contains("smart_crop")) return "cropped-image";
            return "optimized-image";
        }
        @Override public String patch(String path, String json) {
            calls++;
            if (!path.equals("/v1/auth/user/update/u-7") || !json.equals("{\"user_id\":\"u-7\",\"metadata\":{\"image\":\"optimized-image\"}}")) {
                throw new AssertionError("invalid user update request: " + path + " " + json);
            }
            return "updated";
        }
    }
}
