package com.kangurusiaga.app.data.remote.cloudinary

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class CloudinaryUploadServiceTest {

    @Test
    fun generateSignature_sortsParametersAlphabeticallyAndAppendsSecret() {
        val params = mapOf(
            "type" to "private",
            "timestamp" to "1700000000",
            "folder" to "kanguru-siaga/verifikasi-pmk",
            "public_id" to "pmk_pos_1_1700000000"
        )
        val secret = "test_secret_123"

        val actual = CloudinaryUploadService.generateSignature(params, secret)

        val expectedPayload = "folder=kanguru-siaga/verifikasi-pmk&public_id=pmk_pos_1_1700000000&timestamp=1700000000&type=private$secret"
        val expectedHash = MessageDigest.getInstance("SHA-1")
            .digest(expectedPayload.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        assertThat(actual).isEqualTo(expectedHash)
        assertThat(actual).hasLength(40)
    }
}
