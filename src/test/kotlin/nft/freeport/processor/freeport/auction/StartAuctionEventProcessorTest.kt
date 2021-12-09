package nft.freeport.processor.freeport.auction

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.listener.event.StartAuction
import nft.freeport.processor.freeport.contractEvent
import nft.freeport.processor.freeport.nft.NftEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import javax.inject.Inject

@QuarkusTest
internal class StartAuctionEventProcessorTest {
    @Inject
    internal lateinit var testSubject: StartAuctionEventProcessor

    @Test
    fun `Supports StartAuction event`() {
        assertThat(testSubject.supportedClass, equalTo(StartAuction::class.java))
    }

    @Test
    @TestTransaction
    fun `Process StartAuction event`() {
        //given
        NftEntity(
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            BigInteger.ONE
        ).persist()
        val event = StartAuction(
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            90909090909.toBigInteger(),
            1635422403.toBigInteger()
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-10-28T11:58:06Z")))

        //then
        val e = requireNotNull(AuctionEntity.findAll().firstResult())
        e.apply {
            assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
            assertThat(buyer, equalTo("0x0000000000000000000000000000000000000000"))
            assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
            assertThat(nextBidPrice, equalTo(100000000000.toBigInteger()))
            assertThat(endsAt.epochSecond, equalTo(1635422403L))
            LocalDateTime.ofInstant(endsAt, ZoneOffset.UTC).apply {
                assertThat(year, equalTo(2021))
                assertThat(month, equalTo(Month.OCTOBER))
                assertThat(dayOfMonth, equalTo(28))
                assertThat(hour, equalTo(12))
                assertThat(minute, equalTo(0))
                assertThat(second, equalTo(3))
                assertThat(isSettled, equalTo(false))
            }
        }
    }
}