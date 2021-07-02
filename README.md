# Davinci Smart Contract Events Listener

## Description

Listens for events of Davinci smart contract and holds the state.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Env

|Variable|Description|Default value|
|---|---|---|
|DDC_BOOT_NODE|DDC bootnode|`https://node-0.ddc.dev.cere.network`|
|DDC_PUB_KEY_HEX|Application public key in hex format|`0x16047c8f17b13b15021a1c2be99259fb08dda9bf517746795c6fb5fb3f461178`|
|DDC_SEC_KEY_HEX|Application secret key in hex format|`0x3c18b7b6e1fe67af056e77870c65a019c65a7ed7052d550fcc191ca54d16ca9116047c8f17b13b15021a1c2be99259fb08dda9bf517746795c6fb5fb3f461178`|
|NETWORK_CHAIN_ID|Chain ID of the Blockchain being queried.|`80001`|
|NETWORK_CONTRACT_ADDRESS|Smart Contract address|`0xBb391c59403818A56A4ddb51FeE561c42571aFf4`|
|NETWORK_FIRST_BLOCK_NUMBER|Block which holds first contract transaction (Contract creation)|`15798405`|
|NETWORK_COVALENT_API_KEY|Covalent API key|`ckey_103992e94cd94393beb35d1456d`|
|NETWORK_POLL_INTERVAL|Poll interval|`PT30S`|
|QUARKUS_DATASOURCE_USERNAME|Postgres user|`pg`|
|QUARKUS_DATASOURCE_PASSWORD|Postgres password|`pwd`|
|QUARKUS_DATASOURCE_REACTIVE_URL|Postgres URL|`postgresql://localhost:5432/davinci_sc`|

## Requirements for development

- JDK 11 or higher
- Docker and Docker Compose

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell
docker-compose up -d
./gradlew quarkusDev
```

## Build Docker image

```shell
./gradlew quarkusBuild
```
