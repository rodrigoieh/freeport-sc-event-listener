create table nft
(
    nft_id text        not null primary key,
    minter text        not null,
    supply numeric(78) not null
);

-- currency
insert into nft(nft_id, minter, supply)
values ('0', '0x0000000000000000000000000000000000000000', 10000000000);

create index nft_minter_index
    on nft (minter);

create table nft_cid
(
    id     serial not null primary key,
    nft_id text   not null references nft,
    sender text   not null,
    cid    text   not null
);

create index nft_cid_nft_id_index
    on nft_cid (nft_id);

create table wallet_nft
(
    wallet   text        not null,
    nft_id   text        not null references nft,
    quantity numeric(78) not null,
    primary key (wallet, nft_id)
);

create table auction
(
    id         serial      not null primary key,
    seller     text        not null,
    buyer      text        not null,
    nft_id     text        not null references nft,
    price      numeric(78) not null,
    ends_at    timestamp   not null,
    is_settled boolean     not null
);

create index auction_nft_id_index
    on auction (nft_id);

create index auction_seller_index
    on auction (seller);

create table auction_bid
(
    id         serial      not null primary key,
    auction_id integer     not null references auction,
    buyer      text        not null,
    price      numeric(78) not null,
    timestamp  timestamp   not null
);

create index auction_bid_auction_id_index
    on auction_bid (auction_id);

create table joint_account
(
    account  text not null,
    owner    text not null,
    fraction int  not null,
    primary key (account, owner)
);

create table exchange_rate
(
    cere_units_per_penny numeric(78) not null
);

insert into exchange_rate(cere_units_per_penny)
values (1);

create table make_offer
(
    seller text        not null,
    nft_id text        not null references nft,
    price  numeric(78) not null,
    primary key (seller, nft_id)
);

create table take_offer
(
    id     serial      not null primary key,
    buyer  text        not null,
    seller text        not null,
    nft_id text        not null references nft,
    price  numeric(78) not null,
    amount numeric(78) not null
);

create table nft_royalty
(
    nft_id      text        not null references nft,
    sale_type   smallint    not null,
    beneficiary text        not null,
    sale_cut    integer     not null,
    minimum_fee numeric(78) not null,
    primary key (nft_id, sale_type, beneficiary)
);

create table last_scanned_event_position_by_processor
(
    processor_id text   not null,
    contract     text   not null,
    block        bigint not null,
    event_offset bigint,
    state        text   not null,

    primary key (processor_id, contract)
);

create table wh_events
(
    id          serial primary key,
    entity_name text not null,
    event       text not null,
    payload     text not null
);

create table wh_events_queue_processed
(
    event_id integer references wh_events,
    wh_name  text not null,
    primary key (event_id, wh_name)
);