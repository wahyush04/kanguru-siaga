package com.kangurusiaga.app.data.local.emergency

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.emergency.EmergencySection
import org.junit.Test
import java.io.File

class EmergencyJsonParserTest {

    @Test
    fun parse_bblrEmergencyModulesJson_returnsAllSevenModulesInCorrectOrder() {
        val jsonFile = File("src/main/assets/education/bblr_emergency_modules.json")
        val jsonString = jsonFile.readText()

        val modules = EmergencyJsonParser.parse(jsonString)

        assertThat(modules).hasSize(7)
        assertThat(modules.map { it.order }).containsExactly(1, 2, 3, 4, 5, 6, 7).inOrder()

        val expectedIds = listOf(
            "emergency_01", "emergency_02", "emergency_03", "emergency_04",
            "emergency_05", "emergency_06", "emergency_07"
        )
        assertThat(modules.map { it.id }).containsExactlyElementsIn(expectedIds).inOrder()

        val expectedTitles = listOf(
            "Gangguan Pernapasan",
            "Perubahan Warna Kulit",
            "Bayi Sulit Dibangunkan",
            "Kesulitan Menyusu",
            "Kejang",
            "Suhu Tubuh Tidak Normal",
            "Kondisi Lainnya"
        )
        assertThat(modules.map { it.title }).containsExactlyElementsIn(expectedTitles).inOrder()

        modules.forEach { module ->
            assertThat(module.category).isNotEmpty()
            assertThat(module.themeColor).isNotEmpty()
            assertThat(module.iconType).isNotEmpty()
            assertThat(module.heroDrawable).isNotEmpty()
            assertThat(module.heroTag).isNotEmpty()
            assertThat(module.sections).isNotEmpty()
        }
    }

    @Test
    fun parse_module1_gangguanPernapasan_containsExpectedSectionsAndItems() {
        val jsonFile = File("src/main/assets/education/bblr_emergency_modules.json")
        val jsonString = jsonFile.readText()

        val modules = EmergencyJsonParser.parse(jsonString)
        val module1 = modules.first { it.id == "emergency_01" }

        assertThat(module1.title).isEqualTo("Gangguan Pernapasan")
        assertThat(module1.iconType).isEqualTo("lungs")
        assertThat(module1.themeColor).isEqualTo("rose")

        val symptoms = module1.sections.filterIsInstance<EmergencySection.SymptomsList>().firstOrNull()
        assertThat(symptoms).isNotNull()
        assertThat(symptoms?.title).isEqualTo("Tanda yang perlu diperhatikan")
        assertThat(symptoms?.items).containsExactly(
            "Napas terlihat cepat atau sulit",
            "Tarikan dinding dada ke dalam",
            "Hidung kembang kempis",
            "Suara napas tidak normal",
            "Bayi tampak gelisah atau lemas"
        ).inOrder()

        val alert = module1.sections.filterIsInstance<EmergencySection.ActionAlert>().firstOrNull()
        assertThat(alert).isNotNull()
        assertThat(alert?.title).isEqualTo("Apa yang harus dilakukan?")
        assertThat(alert?.message).isEqualTo("Segera hubungi tenaga kesehatan atau bawa bayi ke fasilitas layanan kesehatan terdekat.")
    }

    @Test
    fun parse_allModules_haveActionAlertWithMedicalGuidance() {
        val jsonFile = File("src/main/assets/education/bblr_emergency_modules.json")
        val jsonString = jsonFile.readText()

        val modules = EmergencyJsonParser.parse(jsonString)

        modules.forEach { module ->
            val alert = module.sections.filterIsInstance<EmergencySection.ActionAlert>().firstOrNull()
            assertThat(alert).isNotNull()
            assertThat(alert?.title).isEqualTo("Apa yang harus dilakukan?")
            assertThat(alert?.message).isNotEmpty()
        }
    }
}
