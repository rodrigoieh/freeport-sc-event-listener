package nft.freeport.listener.position.dto

/**
 * Last processed data position.
 */
data class ProcessedEventPosition(
    val block: Long,

    /**
     * offset of the last processed event
     * null means that we haven't handled any event from this block yet
     */
    val offset: Long?,

    val currentState: ProcessingBlockState,
)