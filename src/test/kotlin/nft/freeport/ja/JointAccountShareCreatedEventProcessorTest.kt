package nft.freeport.ja

import io.quarkus.test.junit.QuarkusTest
import nft.freeport.event.JointAccountShareCreated
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class JointAccountShareCreatedEventProcessorTest {
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
            "2021-07-08T00:47:30Z",
            "0xcafebabe",
            "0x123",
            "0xabc",
            123
        )

        //when
        testSubject.process(event)

        //then
        val e = JointAccountEntity.findById(JointAccountEntityId("0x123", "0xabc"))
        assertThat(e, notNullValue())
        assertThat(e?.fraction, equalTo(123))
    }
}
