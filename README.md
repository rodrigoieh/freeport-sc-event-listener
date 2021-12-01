# Freeport Smart Contract Events Processor

## Description

Listens for events of Feeport smart contracts, calculates the state and stores it in DDC, Freeport database and CMS.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Env

|Variable|Description|Default value|
|---|---|---|
|DDC_ENABLED|Is DDC integration enabled|`false` for `dev` profile, `true` for `prod` profile|
|DDC_BOOT_NODES|DDC boot nodes|`http://localhost:8888`|
|DDC_PUB_KEY_HEX|Application public key in hex format|`0xcafebabe`|
|DDC_SEC_KEY_HEX|Application secret key in hex format|`0xcafebabe`|
|NETWORK_CHAIN_ID|Chain ID of the Blockchain being queried.|`80001`|
|NETWORK_COVALENT_API_KEY|Covalent API key|`some test key`|
|NETWORK_POLL_INTERVAL|Poll interval|`PT1S`|
|CONTRACTS_FREEPORT_ADDRESS|Freeport Smart Contract address|`0xd1EdBAC660307c5B6d22E678FB5e22668C70Ad96`|
|CONTRACTS_FREEPORT_FIRST_BLOCK_NUMBER|Freeport Smart Contract first block number|`20997893`|
|CONTRACTS_FIAT_GATEWAY_ADDRESS|Fiat Gateway Smart Contract address|`0x1f8eC932B6ec39A0326b74E9648A158F88B24082`|
|CONTRACTS_FIAT_GATEWAY_FIRST_BLOCK_NUMBER|Fiat Gateway Smart Contract first block number|`20998037`|
|CONTRACTS_AUCTION_ADDRESS|Auction Smart Contract address|`0xd7cd23C84F9109F57f13eF28319e8787628DD7ad`|
|CONTRACTS_AUCTION_FIRST_BLOCK_NUMBER|Auction Smart Contract first block number|`21074782`|
|CONTRACTS_NFT_ATTACHMENT_ADDRESS|NFT Attachment Smart Contract address|`0x270693f873287a39172856Ad8cfbCd79b040b287`|
|CONTRACTS_NFT_ATTACHMENT_FIRST_BLOCK_NUMBER|NFT Attachment Smart Contract first block number|`21202148`|
|QUARKUS_DATASOURCE_USERNAME|Postgres user||
|QUARKUS_DATASOURCE_PASSWORD|Postgres password||
|QUARKUS_DATASOURCE_JDBC_URL|Postgres URL||
|CMS_ENABLED|Is CMS integration enabled|`false`|
|CMS_BASE_URL|CMS base URL|`http://localhost:8888`|
|CMS_LOGIN|CMS login|`api-user`|
|CMS_PASSWORD|CMS password|`api-password`|
|CMS_ROUTES_NFT|NFT collection route|`creator-nfts`|
|CMS_ROUTES_NFT_CID|NFT CID collection route|`creator-nft-cids`|
|CMS_ROUTES_MAKE_OFFER|Make offer collection route|`creator-make-offers`|
|CMS_ROUTES_TAKE_OFFER|Take offer collection route|`.creator-take-offers`|
|CMS_ROUTES_AUCTION|Auction collection route|`creator-auctions`|
|CMS_ROUTES_AUCTION_BID|Auction bid collection route|`creator-auction-bids`|
|CMS_ROUTES_WALLET|Wallet collection route|`creator-wallet-nfts`|
|CMS_ROUTES_EXCHANGE_RATE|Exchange rate route|`creator-exchange-rate`|

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
