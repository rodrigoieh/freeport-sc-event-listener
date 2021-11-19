# Freeport Smart Contract Events Listener

## Description

Listens for events of Feeport smart contracts, calculates the state and stores it in DDC and Freeport database.

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
|WEBHOOKS_ENABLED|Is CDC webhooks enabled|`false`|
|WEBHOOKS_WEBHOOKS_NAME[{INDEX}]|Webhook name||
|WEBHOOKS_WEBHOOKS_TYPE[{INDEX}]|Webhook type (currently we support only `STRAPI`)||
|WEBHOOKS_WEBHOOKS_BASE_URL[{INDEX}]|Webhook base URL||
|WEBHOOKS_WEBHOOKS_CONFIG[{INDEX}]|JSON config for webhook (see below)||
|WEBHOOKS_WEBHOOKS_ENTITIES[{INDEX}]|JSON config for entities (see below)||

## Webhooks configuration

Application can send data changes to remote servers. Current implementation supports only integration
with [Strapi](https://strapi.io/). Webhooks can be configured in `application.yaml` file or using environment
variables (see above).

Example of webhook configuration (yaml):

```yaml
webhooks:
  enabled: true
  webhooks:
    - name: 'cms'
      type: 'STRAPI'
      base-url: 'https://cms.freeport.dev.cere.network'
      config: '{ "login": "$USER_EMAIL", "password": "$USER_PASSWORD" }'
      entities: '{ "nft": "/content-manager/collection-types/application::freeport-nft.freeport-nft" }'
```

Example of webhook configuration (env):

```yaml
WEBHOOKS_ENABLED=true
  WEBHOOKS_WEBHOOKS_NAME[0]=cms
  WEBHOOKS_WEBHOOKS_TYPE[0]=STRAPI
  WEBHOOKS_WEBHOOKS_BASE_URL[0]=https://cms.freeport.dev.cere.network
WEBHOOKS_WEBHOOKS_CONFIG[0]={ "login": "$USER_EMAIL", "password": "$USER_PASSWORD" }
WEBHOOKS_WEBHOOKS_ENTITIES[0]={ "nft": "/content-manager/collection-types/application::freeport-nft.freeport-nft" }
```

Webhook entities is a key-value (JSON) object that uses supported entity name as a key and path to entity enpoint as
value.
Supported entities:
- `auction`
- `auctionbid`
- `nft`
- `nftcid`
- `walletnft`

Application will send `POST` request to the configured endpoint on entity creation and `PUT` request on entity update.

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
