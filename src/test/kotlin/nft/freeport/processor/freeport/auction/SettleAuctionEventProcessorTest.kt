package nft.freeport.processor.freeport.auction

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.event.SettleAuction
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.contractEvent
import nft.freeport.processor.freeport.nft.NftEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import javax.inject.Inject

@QuarkusTest
internal class SettleAuctionEventProcessorTest {
    @Inject
    internal lateinit var testSubject: SettleAuctionEventProcessor

    @Test
    fun `Supports SettleAuction event`() {
        assertThat(testSubject.supportedClass, equalTo(SettleAuction::class.java))
    }

    @Test
    @TestTransaction
    fun `Process SettleAuction event`() {
        //given
        NftEntity(
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            BigInteger.ONE
        ).persist()
        AuctionEntity(
            id = null,
            seller = "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            buyer = "0x0000000000000000000000000000000000000000",
            nftId = "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            nextBidPrice = 90909090909.toBigInteger(),
            endsAt = Instant.ofEpochSecond(1635422403),
            isSettled = false
        ).persist()
        val event = SettleAuction(
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            200000000000.toBigInteger(),
            "0x63846e2d234e4f854f43423014430b4e131f697b"
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-10-28T12:28:52Z")))

        //then
        requireNotNull(AuctionEntity.findAll().firstResult()).apply {
            assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
            assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
            assertThat(
                nftId,
                equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
            )
            assertThat(nextBidPrice, equalTo(200000000000.toBigInteger()))
            assertThat(endsAt.epochSecond, equalTo(1635424132L))
            LocalDateTime.ofInstant(endsAt, ZoneOffset.UTC).apply {
                assertThat(year, equalTo(2021))
                assertThat(month, equalTo(Month.OCTOBER))
                assertThat(dayOfMonth, equalTo(28))
                assertThat(hour, equalTo(12))
                assertThat(minute, equalTo(28))
                assertThat(second, equalTo(52))
                assertThat(isSettled, equalTo(true))
            }
        }
    }
}