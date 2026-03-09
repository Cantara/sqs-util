# sqs-util

## Purpose
Utility library extending the standard AWS SQS client with strong client-side encryption, message compression, and support for arbitrarily large messages (using S3 for messages exceeding 240 KB). Also supports automatic SNS event publishing when SQS messages are sent.

## Tech Stack
- Language: Java 6+
- Framework: None (library)
- Build: Maven
- Key dependencies: AWS SQS SDK, AWS S3 SDK, JCE (Java Cryptography Extension)

## Architecture
Library that wraps the standard AmazonSQSClient, transparently adding encryption (AES), compression, large message support via S3 overflow, and optional SNS notification on send. All features are applied transparently so existing SQS code can benefit without modification.

## Key Entry Points
- Extended SQS client classes
- `pom.xml` - Maven coordinates: `no.cantara.aws:sqs-util`

## Development
```bash
# Build
mvn clean install

# Test
mvn test
```

## Domain Context
AWS messaging infrastructure. Enhances SQS with enterprise-grade encryption and large message support, used by Cantara services that require secure, reliable message queuing on AWS.
