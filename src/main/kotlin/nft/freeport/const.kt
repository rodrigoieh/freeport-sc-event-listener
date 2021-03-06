package nft.freeport

import java.math.BigInteger

const val SMART_CONTRACT_EVENTS_FREEPORT_TOPIC_NAME = "sc-events-freeport"
const val SMART_CONTRACT_EVENTS_DDC_TOPIC_NAME = "sc-events-ddc"
const val SMART_CONTRACT_EVENTS_CMS_TOPIC_NAME = "sc-events-cms"

const val ENTITY_EVENTS_TOPIC_NAME = "entity-events"

/**
 * it's used when a block doesn't have any events
 */
const val NO_EVENTS_BLOCK_OFFSET = -1L

const val ZERO_ADDRESS = "0x0000000000000000000000000000000000000000"

// 2^53-1
val CURRENCY_TOKEN_SUPPLY: BigInteger = BigInteger.valueOf(9007199254740991)

const val CURRENCY_TOKEN_ID = "0"

const val DDC_PROCESSOR_ID = "ddc"
const val FREEPORT_PROCESSOR_ID = "freeport"
const val CMS_PROCESSOR_ID = "cms"