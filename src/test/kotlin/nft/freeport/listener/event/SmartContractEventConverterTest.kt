package nft.freeport.listener.event

import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.shouldBe
import nft.freeport.covalent.dto.ContractEvent
import nft.freeport.covalent.dto.ContractEventParam
import nft.freeport.covalent.dto.DecodedContractEvent
import nft.freeport.listener.AbiDecoder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class SmartContractEventConverterTest {
    private val testSubject = SmartContractEventConverter(AbiDecoder())
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Convert BidOnAuction`() {
        //given
        val source = ContractEvent(
            "2021-10-28T11:58:10Z",
            20744693,
            "0x4edd9e97e70f5410c09570d75807136318e34b23edc9e5efdea275a96b436458",
            listOf(
                "0x39e9b26db60de3ca88f045fdd8954028f1bbd0c6e2ff124121cb5a03da370191",
                "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
                "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
            ),
            "0x0000000000000000000000000000000000000000000000000000002e90edd00000000000000000000000000000000000000000000000000000000000617a931b00000000000000000000000063846e2d234e4f854f43423014430b4e131f697b",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (seller, nftId, price, closeTimeSec, buyer) = event as BidOnAuction

        //then
        checkEventNameJsonField(event, "BidOnAuction")
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(
            nftId,
            equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
        )
        assertThat(price, equalTo(200000000000.toBigInteger()))
        assertThat(closeTimeSec, equalTo(1635423003.toBigInteger()))
        assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
    }

    @Test
    fun `Convert SettleAuction`() {
        //given
        val source = ContractEvent(
            "2021-10-28T12:28:52Z",
            20745586,
            "0x48b22b9351de03360edaa6de9fc1255f85dc8c669875bbf8e30eb6c357ea5483",
            listOf(
                "0xfe2c1531a975fce0584787c5e2643df8c1fe92f870c9dbadb24e366e31e79f44",
                "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
                "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
            ),
            "0x0000000000000000000000000000000000000000000000000000002e90edd00000000000000000000000000063846e2d234e4f854f43423014430b4e131f697b",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (seller, nftId, price, buyer) = event as SettleAuction

        //then
        checkEventNameJsonField(event, "SettleAuction")
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(
            nftId,
            equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
        )
        assertThat(price, equalTo(200000000000.toBigInteger()))
        assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
    }

    @Test
    fun `Convert StartAuction`() {
        //given
        val source = ContractEvent(
            "2021-10-28T11:58:06Z",
            20744691,
            "0xcb5e3b549f6db27b056fe5832b4777ba9f48fd8cb16ff743899c4981fc390806",
            listOf(
                "0x5135842dc9522996ca3d92189d0ded7e70ecbfc5545c115def0c7bdb9ee41f2b",
                "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
                "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
            ),
            "0x000000000000000000000000000000000000000000000000000000152a9aa45d00000000000000000000000000000000000000000000000000000000617a90c3",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (seller, nftId, price, closeTimeSec) = event as StartAuction

        //then
        checkEventNameJsonField(event, "StartAuction")
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
        assertThat(price, equalTo(90909090909.toBigInteger()))
        assertThat(closeTimeSec, equalTo(1635422403.toBigInteger()))
    }

    @Test
    fun `Convert JointAccountShareCreated`() {
        //given
        val source = ContractEvent(
            "2021-07-09T10:51:42Z",
            16124507,
            "0xfe90c2a74aaf2dd3efd1bf52e5b77582a3801deac70f02bca5a7db372838db18",
            listOf(
                "0x006fb9851f1fd2fbc9fa36680d17e1254999a38e5f3c76c3a1ecc126a464601b",
                "0x000000000000000000000000db9875e9a78b8e14bdb602b7b4c12dac4ea7c77c",
                "0x0000000000000000000000006d2b28389d3153689c57909829dfcf6241d36388"
            ),
            "0x0000000000000000000000000000000000000000000000000000000000002328",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (account, owner, fraction) = event as JointAccountShareCreated

        //then
        checkEventNameJsonField(event, "JointAccountShareCreated")
        assertThat(account, equalTo("0xdb9875e9a78b8e14bdb602b7b4c12dac4ea7c77c"))
        assertThat(owner, equalTo("0x6d2b28389d3153689c57909829dfcf6241d36388"))
        assertThat(fraction, equalTo(9000))
    }

    @Test
    fun `Convert AttachToNFT`() {
        //given
        val source = ContractEvent(
            "2021-11-08T10:50:36Z",
            21202410,
            "0xf949e431a856e56d59f5b450c9bcf2a7dd1346fd54e6cdce5e24fb3fe88c5480",
            listOf(
                "0xcb0dbc631188ff7e4c5831ec907b2d9ca2786dd0314af3e43a7269821a19e2b4",
                "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
                "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
            ),
            "0x1122334455667788990011223344556677889900112233445566778899001122",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (sender, nftId, cid) = event as AttachToNFT

        //then
        checkEventNameJsonField(event, "AttachToNFT")
        assertThat(sender, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802"))
        assertThat(cid, equalTo("QmPVXtR5URQHHAT8dqjRUJoNkBUtgyniwJeca8qgG7WHNR"))
    }

    @Test
    fun `Convert TransferBatch`() {
        //given
        val source = ContractEvent(
            "2021-07-08T00:47:30Z",
            1,
            "0xcafebabe",
            listOf("0x4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb"),
            "",
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
            ),
            0
        )

        //when
        val event = testSubject.convert(source)
        val (operator, from, to, ids, amounts) = event as TransferBatch

        //then
        checkEventNameJsonField(event, "TransferBatch")
        assertThat(operator, equalTo("0x123"))
        assertThat(from, equalTo("0xabc"))
        assertThat(to, equalTo("0xdef"))
        assertThat(ids, equalTo(listOf("111", "222")))
        assertThat(amounts, equalTo(listOf(BigInteger.ONE, BigInteger.TEN)))
    }

    @Test
    fun `Convert TransferSingle`() {
        //given
        val source = ContractEvent(
            "2021-07-08T00:47:30Z",
            1,
            "0xcafebabe",
            listOf("0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62"),
            "",
            DecodedContractEvent(
                "TransferBatch", listOf(
                    ContractEventParam("_operator", TextNode("0x123")),
                    ContractEventParam("_from", TextNode("0xabc")),
                    ContractEventParam("_to", TextNode("0xdef")),
                    ContractEventParam("_id", TextNode("111")),
                    ContractEventParam("_amount", TextNode("10"))
                )
            ),
            0
        )

        //when
        val event = testSubject.convert(source)
        val (operator, from, to, id, amount) = event as TransferSingle

        //then
        checkEventNameJsonField(event, "TransferSingle")
        assertThat(operator, equalTo("0x123"))
        assertThat(from, equalTo("0xabc"))
        assertThat(to, equalTo("0xdef"))
        assertThat(id, equalTo("111"))
        assertThat(amount, equalTo(BigInteger.TEN))
    }

    @Test
    fun `Convert MakeOffer`() {
        //given
        val source = ContractEvent(
            "2021-09-08T16:14:53Z",
            18673336,
            "0xb66beed3fa2fccb05189a10e2753804c6e8a9af77d597174557c5be366d6de1f",
            listOf(
                "0x040259e2f9c7930380b3a5c979ad8a30ecf8d344d3bcdb149e2c454ab85fcd8f",
                "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
                "0x51c5590504251a5993ba6a46246f87fa0eae589700000000000000000000000a"
            ),
            "0x000000000000000000000000000000000000000000000000000001d1a94a2000",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (seller, nftId, price) = event as MakeOffer

        //then
        checkEventNameJsonField(event, "MakeOffer")
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(nftId, equalTo("36986023997667029293600386870102381703350581417154820997167315324276546994186"))
        assertThat(price, equalTo(2000000000000.toBigInteger()))
    }

    @Test
    fun `Convert SetExchangeRate`() {
        //given
        val source = ContractEvent(
            "2021-09-08T08:37:55Z",
            18673605,
            "0x73300b05794c305a12696a17db2de8b4dd3a78ff913ab4634657044a80803aa9",
            listOf(
                "0x1d5de90e7c5b244ac5797698b15fe80a92524d933dafd79e001daf844555fb1c",
            ),
            "0x000000000000000000000000000000000000000000000000000000003b9aca00",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (cereUnitsPerPenny) = event as SetExchangeRate

        //then
        checkEventNameJsonField(event, "SetExchangeRate")
        assertThat(cereUnitsPerPenny, equalTo(1000000000.toBigInteger()))
    }

    @Test
    fun `Convert TakeOffer`() {
        //given
        val source = ContractEvent(
            "2021-09-08T16:14:59Z",
            18673605,
            "0xe36d128601568807229048eb55c6d64bc278233e14d38e23c3c5e7673e475260",
            listOf(
                "0xe90359125940e7c9b26005d19d4d8a2a5335ea8bef094bcd61bb8f8091cad117",
                "0x00000000000000000000000063846e2d234e4f854f43423014430b4e131f697b",
                "0x00000000000000000000000051c5590504251a5993ba6a46246f87fa0eae5897",
                "0x51c5590504251a5993ba6a46246f87fa0eae589700000001000000000000000a"
            ),
            "0x00000000000000000000000000000000000000000000000000000574fbde60000000000000000000000000000000000000000000000000000000000000000001",
            null,
            0
        )

        //when
        val event = testSubject.convert(source)
        val (buyer, seller, nftId, price, amount) = event as TakeOffer

        //then
        checkEventNameJsonField(event, "TakeOffer")
        assertThat(buyer, equalTo("0x63846e2d234e4f854f43423014430b4e131f697b"))
        assertThat(seller, equalTo("0x51c5590504251a5993ba6a46246f87fa0eae5897"))
        assertThat(
            nftId,
            equalTo("36986023997667029293600386870102381703350581417154820997185762068350256545802")
        )
        assertThat(price, equalTo(6000000000000.toBigInteger()))
        assertThat(amount, equalTo(BigInteger.ONE))
    }

    @Test
    fun `Convert RoyaltiesConfigured`() {
        //given
        val source = ContractEvent(
            "2021-07-14T08:50:24Z",
            16329438,
            "0x0f486842c2b6282d876f3f92b72e43b8d104f1db3053fa92f05d2f90359fce2d",
            listOf(
                "0x3df2272e42784a2455bbbdcdf25ae0f67d41f801896514cc4cd255aa8ee75e4c",
                "0x1bf6fca28253a1257e4b5b3440f7fbe0c59d1546000000010000000000000001"
            ),
            "0x0000000000000000000000001bf6fca28253a1257e4b5b3440f7fbe0c59d1546000000000000000000000000000000000000000000000000000000000000000500000000000000000000000000000000000000000000000000000000000000060000000000000000000000008aaab81aff26d0f4e34520b87c608b183dcf5bb800000000000000000000000000000000000000000000000000000000000000070000000000000000000000000000000000000000000000000000000000000008",
            null,
            0
        )

        //when
        val result = testSubject.convert(source) as RoyaltiesConfigured

        //then
        checkEventNameJsonField(result, "RoyaltiesConfigured")
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

    private fun checkEventNameJsonField(event: SmartContractEvent, expectedValue: String) {
        val json = objectMapper.writeValueAsString(event)

        objectMapper.readTree(json).get("eventName").textValue() shouldBe expectedValue
    }
}