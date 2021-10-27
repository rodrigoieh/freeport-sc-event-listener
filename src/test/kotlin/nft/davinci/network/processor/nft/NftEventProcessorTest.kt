package nft.davinci.network.processor.nft

import kotlinx.coroutines.runBlocking
import nft.davinci.ddc.DdcService
import nft.davinci.event.NftMinted
import nft.davinci.event.NftTransferred
import nft.davinci.nft.NftRepository
import nft.davinci.nft.WalletNftRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyNoMoreInteractions
import java.math.BigInteger

internal class NftEventProcessorTest {
    private val ddcService: DdcService = mock()
    private val nftRepository: NftRepository = mock()
    private val walletNftRepository: WalletNftRepository = mock()

    private val testSubject = NftEventProcessor(ddcService, nftRepository, walletNftRepository)

    @Test
    fun `Process NFT minted event`() {
        //given
        val event = NftMinted("0x123", "0xabc", "123", BigInteger.TEN)

        //when
        runBlocking { testSubject.onNftEvent(event, "2021-07-08T00:47:30Z", "0xcafebabe") }

        //then
        inOrder(ddcService, nftRepository, walletNftRepository) {
            verifyBlocking(ddcService) { sendNftEvent(event, "2021-07-08T00:47:30Z", "0xcafebabe") }
            verifyBlocking(nftRepository) { create(event.nftId, event.minter, event.quantity) }
            verifyBlocking(walletNftRepository) { updateQuantity(event.minter, event.nftId, event.quantity) }
        }
        verifyNoMoreInteractions(ddcService, nftRepository, walletNftRepository)
    }

    @Test
    fun `Process NFT transferred event`() {
        //given
        val event = NftTransferred("0x123", "0xabc", "0xdef", "123", BigInteger.TEN)

        //when
        runBlocking { testSubject.onNftEvent(event, "2021-07-08T00:47:30Z", "0xcafebabe") }

        //then
        inOrder(ddcService, walletNftRepository) {
            verifyBlocking(ddcService) { sendNftEvent(event, "2021-07-08T00:47:30Z", "0xcafebabe") }
            verifyBlocking(walletNftRepository) { updateQuantity(event.from, event.nftId, -event.quantity) }
            verifyBlocking(walletNftRepository) { updateQuantity(event.to, event.nftId, event.quantity) }
        }
        verifyNoMoreInteractions(ddcService, nftRepository, walletNftRepository)
    }
}
