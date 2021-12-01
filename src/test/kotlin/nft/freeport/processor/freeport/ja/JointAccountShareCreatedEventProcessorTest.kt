package nft.freeport.processor.freeport.ja

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.AbstractIntegrationTest
import nft.freeport.listener.event.JointAccountShareCreated
import nft.freeport.listener.event.SmartContractEventData
import nft.freeport.processor.freeport.contractEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class JointAccountShareCreatedEventProcessorTest : AbstractIntegrationTest() {
    @Inject
    internal lateinit var testSubject: JointAccountShareCreatedEventProcessor

    @Test
    fun `Supports JointAccountShareCreated event`() {
        assertThat(testSubject.supportedClass, equalTo(JointAccountShareCreated::class.java))
    }

    @Test
    fun `Process joint accounts share created event`() {
        //given
        val event = JointAccountShareCreated(
            "0x123",
            "0xabc",
            123
        )

        //when
        testSubject.process(SmartContractEventData("some-contract", event, contractEvent("2021-07-08T00:47:30Z")))

        //then
        val e = JointAccountEntity.findById(JointAccountEntityId("0x123", "0xabc"))
        assertThat(e, notNullValue())
        assertThat(e?.fraction, equalTo(123))
    }
}
