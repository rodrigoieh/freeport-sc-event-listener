package nft.freeport.auction

import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.StartAuction
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
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
        assertThat(testSubject.supportedClass, Matchers.equalTo(StartAuction::class.java))
    }

    @Test
    @TestTransaction
    fun `Process StartAuction event`() {
        //given
        val event = StartAuction(
            "2021-10-28T11:58:06Z",
            "0xcb5e3b549f6db27b056fe5832b4777ba9f48fd8cb16ff743899c4981fc390806",
            "0x51c5590504251a5993ba6a46246f87fa0eae5897",
            "36986023997667029293600386870102381703350581417154820997185762068350256545802",
            90909090909.toBigInteger(),
            1635422403.toBigInteger()
        )

        //when
        testSubject.process(event)

        //then
        val e = requireNotNull(AuctionEntity.findAll().firstResult())
        e.apply {
            assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
            assertThat(buyer, equalTo("0x0000000000000000000000000000000000000000"))
            assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
            assertThat(price, equalTo(90909090909.toBigInteger()))
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