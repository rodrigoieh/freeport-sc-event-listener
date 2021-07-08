package nft.davinci.network.converter

import com.fasterxml.jackson.databind.node.TextNode
import nft.davinci.event.JointAccountShareCreated
import nft.davinci.network.dto.ContractEventParam
import nft.davinci.network.dto.DecodedContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class JointAccountShareCreatedConverterTest {
    private val testSubject = JointAccountShareCreatedConverter()

    @Test
    fun `Supports JointAccountShareCreated event`() {
        assertThat(testSubject.supportedClass, equalTo(JointAccountShareCreated::class.java))
    }

    @Test
    fun `Convert event`() {
        //given
        val source = DecodedContractEvent(
            "JointAccountShareCreated", listOf(
                ContractEventParam("_account", TextNode("0x123")),
                ContractEventParam("_owner", TextNode("0xabc")),
                ContractEventParam("_fraction", TextNode("10"))
            )
        )

        //when
        val (account, owner, fraction) = testSubject.convert(source)

        //then
        assertThat(account, equalTo("0x123"))
        assertThat(owner, equalTo("0xabc"))
        assertThat(fraction, equalTo(10))
    }
}
