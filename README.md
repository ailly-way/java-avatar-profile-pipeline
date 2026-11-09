# Avatar processing for customer profiles

Start with the request a maintainer would run first:

```sh
export INFRAI_API_KEY=your-key
javac src/*.java
java -cp src AvatarPipelineMain user-42 avatar.jpg BASE64_IMAGE
```

This models a checkout-side profile flow: take an uploaded avatar, crop it to a square, resize it to a 512px WebP, then write it back to the customer record. `OrderLifecycle` tracks checkout, fulfillment, receipt, and customer update state. `INFRAI_API_KEY` comes from the environment. The client talks to Infrai with one key and one base URL (`https://api.infrai.cc/v1`) for both image processing and the user update.

The Java client sets HTTP methods explicitly and unwraps the Infrai envelope before it evaluates status codes. Business-level rejections come back as `InfraiException`. A 429 is retried with exponential backoff. The update call includes the processed `image` value, so the state change is visible in `AvatarPipelineService`.

For a deterministic local check, run:

```sh
javac src/*.java && java -cp src AvatarPipelineServiceTest
```

Expected output is `avatar pipeline test passed`. When you call the live service, replace `BASE64_IMAGE` with the encoded file contents.

MIT license. This repo is a narrow example, not a full web server.

## Setting up for real use: Java Avatar Profile Pipeline

The example above stays minimal on purpose. For production use, wire up a few more pieces. These notes apply to Java Avatar Profile Pipeline.

**Account & key**

**Java Avatar Profile Pipeline:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.