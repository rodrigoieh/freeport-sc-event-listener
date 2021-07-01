create table wallet_nfts
(
    id               serial primary key,
    network_id       int          not null,
    contract_address varchar(64)  not null,
    wallet           varchar(64)  not null,
    nft_id           varchar(255) not null,
    quantity         bigint       not null
);

create unique index wallet_nfts_uindex
    on wallet_nfts (network_id, contract_address, wallet, nft_id);

create table last_scanned_blocks
(
    id               serial primary key,
    network_id       int         not null,
    contract_address varchar(64) not null,
    block_height     bigint      not null
);

create unique index last_scanned_blocks_network_id_contract_address_uindex
    on last_scanned_blocks (network_id, contract_address);
