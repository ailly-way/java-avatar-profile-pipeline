public record OrderLifecycle(String checkoutId, String fulfillment, String receiptId, String customerUpdate) {
    public static OrderLifecycle paid(String checkoutId, String receiptId) {
        return new OrderLifecycle(checkoutId, "ready_for_fulfillment", receiptId, "payment_received");
    }
}
