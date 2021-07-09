package nft.davinci.network.converter

import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import nft.davinci.event.TransferBatch
import nft.davinci.network.dto.ContractEvent
import nft.davinci.network.dto.ContractEventParam
import nft.davinci.network.dto.DecodedContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class TransferBatchConverterTest {
    private val testSubject = TransferBatchConverter()
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Supports TransferBatch event`() {
        assertThat(testSubject.supportedClass, equalTo(TransferBatch::class.java))
    }

    @Test
    fun `Convert event`() {
        //given
        val source = ContractEvent(
            "2021-07-08T00:47:30Z",
            1,
            "0xcafebabe",
            DecodedContractEvent(
                "TransferBatch", listOf(
                    ContractEventParam("_operator", TextNode("0x123")),
                    ContractEventParam("_from", TextNode("0xabc")),
                    ContractEventParam("_to", TextNode("0xdef")),
                    ContractEventParam(
                        "_ids", objectMapper.createArrayNode().addAll(
                            listOf(
                                objectMapper.createObjectNode().put("value", "111"),
                                objectMapper.createObjectNode().put("value", "222")
                            )
                        )
                    ),
                    ContractEventParam(
                        "_amounts", objectMapper.createArrayNode().addAll(
                            listOf(
                                objectMapper.createObjectNode().put("value", "1"),
                                objectMapper.createObjectNode().put("value", "10")
                            )
                        )
                    )
                )
            )
        )

        //when
        val (blockSignedAt, txHash, operator, from, to, ids, amounts) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-07-08T00:47:30Z"))
        assertThat(txHash, equalTo("0xcafebabe"))
        assertThat(operator, equalTo("0x123"))
        assertThat(from, equalTo("0xabc"))
        assertThat(to, equalTo("0xdef"))
        assertThat(ids, equalTo(listOf("111", "222")))
        assertThat(amounts, equalTo(listOf(BigInteger.ONE, BigInteger.TEN)))
    }
}
