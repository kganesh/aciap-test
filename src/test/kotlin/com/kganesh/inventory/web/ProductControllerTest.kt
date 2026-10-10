package com.kganesh.inventory.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTest(@Autowired private val mvc: MockMvc) {

    private fun createWidget() {
        mvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"sku":"SKU-1","name":"Widget","priceCents":999,"stock":10}"""
        }.andExpect { status { isCreated() } }
    }

    @Test
    fun `create then get a product`() {
        createWidget()
        mvc.get("/products/SKU-1").andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Widget") }
            jsonPath("$.stock") { value(10) }
        }
    }

    @Test
    fun `unknown sku is 404`() {
        mvc.get("/products/NOPE").andExpect { status { isNotFound() } }
    }

    @Test
    fun `duplicate sku is 409`() {
        createWidget()
        mvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"sku":"SKU-1","name":"Again","priceCents":1}"""
        }.andExpect { status { isConflict() } }
    }

    @Test
    fun `blank name is 400`() {
        mvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"sku":"SKU-2","name":"","priceCents":1}"""
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun `stock cannot go negative`() {
        createWidget()
        mvc.post("/products/SKU-1/stock") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"delta":-11}"""
        }.andExpect { status { isConflict() } }
    }

    @Test
    fun `quote applies discount`() {
        createWidget()
        mvc.get("/products/SKU-1/quote?quantity=3&discountPercent=15").andExpect {
            status { isOk() }
            jsonPath("$.totalCents") { value(2547) }
        }
    }
}
