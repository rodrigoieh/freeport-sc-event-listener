package nft.davinci.network.processor.ja

import kotlinx.coroutines.runBlocking
import nft.davinci.event.JointAccountShareCreated
import nft.davinci.ja.JointAccountRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyBlocking

internal class JointAccountShareCreatedEventProcessorTest {
    private val jointAccountRepository: JointAccountRepository = mock()

    private val testSubject = JointAccountShareCreatedEventProcessor(jointAccountRepository)

    @Test
    fun `Supports JointAccountShareCreated event`() {
        assertThat(testSubject.supportedClass, equalTo(JointAccountShareCreated::class.java))
    }

    @Test
    fun `Process joint accouts share created event`() {
        //given
        val event = JointAccountShareCreated("0x123", "0xabc", 123)

        //when
        runBlocking { testSubject.process(event) }

        //then
        verifyBlocking(jointAccountRepository) { create("0x123", "0xabc", 123) }
    }
}
