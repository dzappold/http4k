package org.http4k.contract.jsonschema.v3

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.http4k.contract.openapi.v3.JavaBean
import org.junit.jupiter.api.Test

class SimpleLookupTest {

    data class KotlinBean(
        val nonNullable: String = "hello",
        @JsonPropertyDescription("A field description")
        val withMeta: String = "withMeta",
        val aNullable: String? = "aNullable"
    )

    @Test
    fun `finds value from object`() {
        assertThat("nonNullable", SimpleLookup()(KotlinBean(), "nonNullable"), equalTo(Field("hello", false, FieldMetadata.empty)))
        assertThat("aNullable", SimpleLookup()(KotlinBean(), "aNullable"), equalTo(Field("aNullable", true, FieldMetadata.empty)))
        assertThat(
            "withMeta",
            SimpleLookup(metadataRetrievalStrategy = JacksonFieldMetadataRetrievalStrategy)(KotlinBean(), "withMeta"),
            equalTo(Field("withMeta", false, FieldMetadata(mapOf("description" to "A field description"))))
        )
    }

    @Test
    fun `finds value from java object`() {
        assertThat("nonNullable", SimpleLookup()(JavaBean("hello"), "name"), equalTo(Field("hello", true, FieldMetadata.empty)))
    }

    @Test
    fun `recovers and responds with empty FieldMetadata when there are generics in the class`() {
        assertThat("non existent", { SimpleLookup()(KotlinBean(), "non existent") }, throws<NoFieldFound>())
    }
}
