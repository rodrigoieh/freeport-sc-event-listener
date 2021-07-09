package nft.davinci.network.converter

import com.fasterxml.jackson.databind.node.TextNode
import nft.davinci.event.TransferSingle
import nft.davinci.network.dto.ContractEvent
import nft.davinci.network.dto.ContractEventParam
import nft.davinci.network.dto.DecodedContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class TransferSingleConverterTest {
    private val testSubject = TransferSingleConverter()

    @Test
    fun `Supports TransferSingle event`() {
        assertThat(testSubject.supportedClass, equalTo(TransferSingle::class.java))
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
                    ContractEventParam("_id", TextNode("111")),
                    ContractEventParam("_amount", TextNode("10"))
                )
            )
        )

        //when
        val (blockSignedAt, txHash, operator, from, to, id, amount) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-07-08T00:47:30Z"))
        assertThat(txHash, equalTo("0xcafebabe"))
        assertThat(operator, equalTo("0x123"))
        assertThat(from, equalTo("0xabc"))
        assertThat(to, equalTo("0xdef"))
        assertThat(id, equalTo("111"))
        assertThat(amount, equalTo(BigInteger.TEN))
    }
}
