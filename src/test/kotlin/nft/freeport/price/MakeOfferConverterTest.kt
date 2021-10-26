package nft.freeport.price

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.network.dto.ContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class MakeOfferConverterTest {
    @Inject
    internal lateinit var testSubject: MakeOfferConverter

    private val source = ContractEvent(
        "2021-09-08T16:14:53Z",
        18673336,
        "0xb66beed3fa2fccb05189a10e2753804c6e8a9af77d597174557c5be366d6de1f",
        listOf(
            "0x040259e2f9c7930380b3a5c979ad8a30ecf8d344d3bcdb149e2c454ab85fcd8f",
            "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
            "0x51c5590504251a5993ba6a46246f87fa0eae589700000000000000000000000a"
        ),
        "0x000000000000000000000000000000000000000000000000000001d1a94a2000",
        null
    )

    @Test
    fun `Can convert JointAccountShareCreated`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val (blockSignedAt, txHash, seller, nftId, price) = testSubject.convert(source)

        //then
        assertThat(blockSignedAt, equalTo("2021-09-08T16:14:53Z"))
        assertThat(
            txHash,
            equalTo("0xb66beed3fa2fccb05189a10e2753804c6e8a9af77d597174557c5be366d6de1f")
        )
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997167315324276546994186"))
        assertThat(price, equalTo(2000000000000.toBigInteger()))
    }
}