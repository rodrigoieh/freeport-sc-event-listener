package nft.freeport.listener.processorsPosition.dto

enum class ProcessingBlockState {
    /**
     * This state is applied when the last event of the block is processed.
     */
    DONE,

    /**
     * This state is applied after processing every event of the block.
     */
    PARTIALLY_DONE,

    /** todo maybe just use [PARTIALLY_DONE] in such a case **/
    /**
     * This state is applied when no events are already processed, but we know about block number.
     * e.g. from application configs.
     */
    NEW,
}