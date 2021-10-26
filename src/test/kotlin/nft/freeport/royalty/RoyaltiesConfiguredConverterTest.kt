package nft.freeport.royalty

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.network.dto.ContractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class RoyaltiesConfiguredConverterTest {
    @Inject
    internal lateinit var testSubject: RoyaltiesConfiguredConverter

    private val source = ContractEvent(
        "2021-07-14T08:50:24Z",
        16329438,
        "0x0f486842c2b6282d876f3f92b72e43b8d104f1db3053fa92f05d2f90359fce2d",
        listOf(
            "0x3df2272e42784a2455bbbdcdf25ae0f67d41f801896514cc4cd255aa8ee75e4c",
            "0x1bf6fca28253a1257e4b5b3440f7fbe0c59d1546000000010000000000000001"
        ),
        "0x0000000000000000000000001bf6fca28253a1257e4b5b3440f7fbe0c59d1546000000000000000000000000000000000000000000000000000000000000000500000000000000000000000000000000000000000000000000000000000000060000000000000000000000008aaab81aff26d0f4e34520b87c608b183dcf5bb800000000000000000000000000000000000000000000000000000000000000070000000000000000000000000000000000000000000000000000000000000008",
        null
    )

    @Test
    fun `Can convert RoyaltiesConfigured`() {
        assertTrue(testSubject.canConvert(source))
    }

    @Test
    fun `Can't convert other events`() {
        assertFalse(testSubject.canConvert(source.copy(rawLogTopics = listOf())))
    }

    @Test
    fun `Convert event`() {
        //when
        val result = testSubject.convert(source)

        //then
        assertThat(result.blockSignedAt, equalTo("2021-07-14T08:50:24Z"))
        assertThat(
            result.txHash,
            equalTo("0x0f486842c2b6282d876f3f92b72e43b8d104f1db3053fa92f05d2f90359fce2d")
        )
        assertThat(
            result.nftId,
            equalTo("12648834910999427420486791714441733200585385323701742277809926298386105892865")
        )
        assertThat(result.primaryRoyaltyAccount, equalTo("0x1bf6fca28253a1257e4b5b3440f7fbe0c59d1546"))
        assertThat(result.primaryRoyaltyCut, equalTo(5))
        assertThat(result.primaryRoyaltyMinimum, equalTo(6.toBigInteger()))
        assertThat(result.secondaryRoyaltyAccount, equalTo("0x8aaab81aff26d0f4e34520b87c608b183dcf5bb8"))
        assertThat(result.secondaryRoyaltyCut, equalTo(7))
        assertThat(result.secondaryRoyaltyMinimum, equalTo(8.toBigInteger()))
    }
}
