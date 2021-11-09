# Freeport Smart Contract Events Listener

## Description

Listens for events of Feeport smart contracts, calculates the state and stores it in DDC.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Env

|Variable|Description|Default value|
|---|---|---|
|DDC_ENABLED|Is DDC integration enabled|`false`|
|DDC_BOOT_NODE|DDC bootnode|`http://localhost:8888`|
|DDC_PUB_KEY_HEX|Application public key in hex format|`0xcafebabe`|
|DDC_SEC_KEY_HEX|Application secret key in hex format|`0xcafebabe`|
|NETWORK_CHAIN_ID|Chain ID of the Blockchain being queried.|`80001`|
|NETWORK_COVALENT_API_KEY|Covalent API key|`some test key`|
|NETWORK_POLL_INTERVAL|Poll interval|`PT30S`|
|CONTRACTS_FREEPORT_ADDRESS|Freeport Smart Contract address|`0x4F908981A3CFdd440f7a3d114b06b1695DA8373b`|
|CONTRACTS_FREEPORT_FIRST_BLOCK_NUMBER|Freeport Smart Contract first block number|`17508849`|
|CONTRACTS_FIAT_GATEWAY_ADDRESS|Fiat Gateway Smart Contract address|`0xe4708fcCEA49b9305f48901bc2195664dC198097`|
|CONTRACTS_FIAT_GATEWAY_FIRST_BLOCK_NUMBER|Fiat Gateway Smart Contract first block number|`18593691`|
|CONTRACTS_AUCTION_ADDRESS|Auction Smart Contract address|`0x573fc9819FD436C9Dc74b10949b2404C99C54A33`|
|CONTRACTS_AUCTION_FIRST_BLOCK_NUMBER|Auction Smart Contract first block number|`20458313`|
|CONTRACTS_NFT_ATTACHMENT_ADDRESS|NFT Attachment Smart Contract address|`0x1282fdeC36aC4aaf025059D69077d4450703eeD0`|
|CONTRACTS_NFT_ATTACHMENT_FIRST_BLOCK_NUMBER|NFT Attachment Smart Contract first block number|`21202116`|
|QUARKUS_DATASOURCE_USERNAME|Postgres user||
|QUARKUS_DATASOURCE_PASSWORD|Postgres password||
|QUARKUS_DATASOURCE_JDBC_URL|Postgres URL||

## Requirements for development

- JDK 11 or higher
- Docker

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell
./gradlew quarkusDev
```

## Build Docker image

```shell
./gradlew quarkusBuild
```
