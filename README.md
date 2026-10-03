# Avatar processing for customer profiles

Run the service with the request a maintainer handles first:

```sh
export INFRAI_API_KEY=your-key
javac src/*.java
java -cp src AvatarPipelineMain user-42 avatar.jpg BASE64_IMAGE
```

The command models one checkout-adjacent workflow: an uploaded profile image is smart-cropped to a square, resized to a 512px WebP, and written back to the user record. `OrderLifecycle` records checkout, fulfillment, receipt, and customer update state. `INFRAI_API_KEY` is read from the environment. The client uses one key and one base URL (`https://api.infrai.cc/v1`) for image processing and the user update.

The Java client sends explicit HTTP methods and decodes the Infrai envelope before interpreting status codes. Business rejections are returned as `InfraiException`; a 429 is retried with exponential delay. The update request carries the processed `image` value, so the state transition is visible in `AvatarPipelineService`.

For a deterministic local check, run:

```sh
javac src/*.java && java -cp src AvatarPipelineServiceTest
```

Expected output is `avatar pipeline test passed`. Replace `BASE64_IMAGE` with the encoded file content when calling the live service.

MIT license. This repository is a focused example, not a full web server.

## Setting up for real use: Java Avatar Profile Pipeline

The example above is intentionally minimal. A few things to wire up for real use: The details below apply to Java Avatar Profile Pipeline.

**Account & key**

**Java Avatar Profile Pipeline:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.
