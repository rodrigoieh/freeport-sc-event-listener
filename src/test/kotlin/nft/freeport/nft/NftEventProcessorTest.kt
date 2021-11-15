package nft.freeport.nft

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.NftMinted
import nft.freeport.event.NftTransferred
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.math.BigInteger
import javax.inject.Inject

@QuarkusTest
internal class NftEventProcessorTest {
    @Inject
    internal lateinit var testSubject: NftEventProcessor

    @Test
    fun `Process NFT minted event`() {
        //given
        val event = NftMinted("0x123", "0xabc", "123", BigInteger.TEN)

        //when
        testSubject.onNftEvent(event, "2021-07-08T00:47:30Z")

        //then
        assertThat(NftEntity.findById(NftEntityId("123", "0xabc")), notNullValue())
        assertThat(WalletNftEntity.findById(WalletNftEntityId("123", "0xabc")), notNullValue())
    }

    @Test
    fun `Process NFT transferred event`() {
        //given
        testSubject.onNftEvent(NftMinted("0x123", "0xabc", "456", BigInteger.TEN), "2021-07-08T00:47:30Z")
        val event = NftTransferred("0x123", "0xabc", "0xdef", "456", BigInteger.ONE)

        //when
        testSubject.onNftEvent(event, "2021-07-08T00:47:30Z")

        //then
        assertThat(WalletNftEntity.findById(WalletNftEntityId("456", "0xabc"))?.quantity, equalTo(9.toBigInteger()))
        assertThat(WalletNftEntity.findById(WalletNftEntityId("456", "0xdef"))?.quantity, equalTo(BigInteger.ONE))
    }

    @Test
    fun `Process native token transfer events`() {
        //given
        val event = NftMinted("0x123", "0xabc", "0", BigInteger.TEN)

        //when
        testSubject.onNftEvent(event, "2021-07-08T00:47:30Z")

        //then
        assertThat(NftEntity.findById(NftEntityId("0", "0xabc")), nullValue())
        assertThat(WalletNftEntity.findById(WalletNftEntityId("0", "0xabc"))?.quantity, equalTo(BigInteger.TEN))
    }
}
