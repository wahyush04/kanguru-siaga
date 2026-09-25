package com.kangurusiaga.app.data.local.emergency

import com.kangurusiaga.app.domain.model.emergency.EmergencySection
import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import org.json.JSONArray
import org.json.JSONObject

object EmergencyJsonParser {

    fun parse(jsonString: String): List<EmergencyWarningModule> {
        val root = JSONObject(jsonString)
        val modulesArray = root.getJSONArray("modules")
        val result = mutableListOf<EmergencyWarningModule>()

        for (i in 0 until modulesArray.length()) {
            val moduleObj = modulesArray.getJSONObject(i)
            val id = moduleObj.getString("id")
            val order = moduleObj.getInt("order")
            val title = moduleObj.getString("title")
            val category = moduleObj.optString("category", "Tanda Kegawatan BBLR")
            val iconType = moduleObj.optString("iconType", "other")
            val themeColor = moduleObj.optString("themeColor", "rose")
            val heroDrawable = moduleObj.optString("heroDrawable", "")
            val heroTag = moduleObj.optString("heroTag", "")

            val sections = parseSections(moduleObj.optJSONArray("sections"))

            result.add(
                EmergencyWarningModule(
                    id = id,
                    order = order,
                    title = title,
                    category = category,
                    iconType = iconType,
                    themeColor = themeColor,
                    heroDrawable = heroDrawable,
                    heroTag = heroTag,
                    sections = sections
                )
            )
        }

        return result.sortedBy { it.order }
    }

    private fun parseSections(sectionsArray: JSONArray?): List<EmergencySection> {
        if (sectionsArray == null) return emptyList()
        val sections = mutableListOf<EmergencySection>()

        for (i in 0 until sectionsArray.length()) {
            val sectionObj = sectionsArray.getJSONObject(i)
            val type = sectionObj.optString("type", "")

            when (type) {
                "symptoms_list" -> {
                    val title = sectionObj.optString("title", "Tanda yang perlu diperhatikan")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<String>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            items.add(itemsArray.getString(j))
                        }
                    }
                    sections.add(EmergencySection.SymptomsList(title, items))
                }

                "action_alert" -> {
                    val title = sectionObj.optString("title", "Apa yang harus dilakukan?")
                    val message = sectionObj.optString("message", "")
                    val themeColor = sectionObj.optString("themeColor", "amber")
                    sections.add(EmergencySection.ActionAlert(title, message, themeColor))
                }
            }
        }

        return sections
    }
}
