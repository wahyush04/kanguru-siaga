package com.kangurusiaga.app.data.local

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.education.EducationSection
import org.junit.Test
import java.io.File

class EducationJsonParserTest {

    @Test
    fun parse_bblrModulesJson_returnsAllNineModulesInCorrectOrder() {
        val jsonFile = File("src/main/assets/education/bblr_modules.json")
        val jsonString = jsonFile.readText()

        val modules = EducationJsonParser.parse(jsonString)

        assertThat(modules).hasSize(9)
        assertThat(modules.map { it.order }).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9).inOrder()

        val expectedIds = listOf(
            "bblr_01", "bblr_02", "bblr_03", "bblr_04", "bblr_05",
            "bblr_06", "bblr_07", "bblr_08", "bblr_09"
        )
        assertThat(modules.map { it.id }).containsExactlyElementsIn(expectedIds).inOrder()

        val expectedTitles = listOf(
            "Pengertian BBLR",
            "Menjaga Kehangatan Bayi",
            "Kebersihan Bayi dan Lingkungan",
            "Perawatan Tali Pusat",
            "Posisi dan Keamanan Bayi",
            "Pemberian ASI",
            "Pemantauan Kondisi Bayi",
            "Kontrol Sesuai Jadwal",
            "Hal yang Perlu Diperhatikan"
        )
        assertThat(modules.map { it.title }).containsExactlyElementsIn(expectedTitles).inOrder()

        modules.forEach { module ->
            assertThat(module.category).isNotEmpty()
            assertThat(module.readingTimeMinutes).isAtLeast(3)
            assertThat(module.themeColor).isNotEmpty()
            assertThat(module.heroDrawable).isNotEmpty()
            assertThat(module.sections).isNotEmpty()
        }
    }

    @Test
    fun parse_module1_containsExpectedSections() {
        val jsonFile = File("src/main/assets/education/bblr_modules.json")
        val jsonString = jsonFile.readText()

        val modules = EducationJsonParser.parse(jsonString)
        val module1 = modules.first { it.id == "bblr_01" }

        val overview = module1.sections.filterIsInstance<EducationSection.Overview>().firstOrNull()
        assertThat(overview).isNotNull()
        assertThat(overview?.title).isEqualTo("Apa itu BBLR?")
        assertThat(overview?.paragraphs).hasSize(2)

        val classifications = module1.sections.filterIsInstance<EducationSection.ClassificationList>().firstOrNull()
        assertThat(classifications).isNotNull()
        assertThat(classifications?.items).hasSize(3)

        val iconList = module1.sections.filterIsInstance<EducationSection.IconList>().firstOrNull()
        assertThat(iconList).isNotNull()
        assertThat(iconList?.items).hasSize(4)

        val steps = module1.sections.filterIsInstance<EducationSection.StepList>().firstOrNull()
        assertThat(steps).isNotNull()
        assertThat(steps?.items).hasSize(3)

        val callout = module1.sections.filterIsInstance<EducationSection.CalloutBox>().firstOrNull()
        assertThat(callout).isNotNull()
        assertThat(callout?.title).contains("Catatan Kasih")
    }

    @Test
    fun parse_module8_containsScreeningBox() {
        val jsonFile = File("src/main/assets/education/bblr_modules.json")
        val jsonString = jsonFile.readText()

        val modules = EducationJsonParser.parse(jsonString)
        val module8 = modules.first { it.id == "bblr_08" }

        val screening = module8.sections.filterIsInstance<EducationSection.ScreeningBox>().firstOrNull()
        assertThat(screening).isNotNull()
        assertThat(screening?.items).hasSize(2)
        assertThat(screening?.items?.map { it.title }).contains("Skrining Retinopati Prematuritas (ROP)")
    }
}
