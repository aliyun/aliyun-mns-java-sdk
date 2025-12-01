## Changelog

## 2.0.0
- Major version upgrade to 2.0.0
- Add V4 signature functionality
- Support explicit region configuration in CloudAccount (regionId property is now required). It is recommended to use MNSClientBuilder to initialize CloudAccount and MNSClient.
- Use explicitly configured region to construct subscription endpoint/ARN instead of parsing from endpoint URL
- Remove deprecated attributes: MailAttributes, DayuAttributes, SmsAttributes, BatchSmsAttributes
- Remove unused attributes: WebSocketAttributes, PushAttributes
- ⚠️ **Breaking Changes**: 
  - CloudAccount's regionId property is now required. Failure to provide it will result in an exception during client initialization
  - Subscription endpoints now use the explicitly configured regionId rather than attempting to parse region information from the endpoint URL
  - V4 signature functionality requires the explicitly configured regionId to generate more secure v4 signatures
  - In com.aliyun.mns.client.MNSClient, the SetAccountAttributes and GetAccountAttributes methods have been renamed to setAccountAttributes and getAccountAttributes (following standard Java naming conventions)
