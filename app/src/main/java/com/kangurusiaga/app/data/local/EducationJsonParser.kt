package com.kangurusiaga.app.data.local

import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.domain.model.education.EducationSection
import org.json.JSONArray
import org.json.JSONObject

object EducationJsonParser {

    fun parse(jsonString: String): List<EducationModule> {
        val root = JSONObject(jsonString)
        val modulesArray = root.getJSONArray("modules")
        val result = mutableListOf<EducationModule>()

        for (i in 0 until modulesArray.length()) {
            val moduleObj = modulesArray.getJSONObject(i)
            val id = moduleObj.getString("id")
            val order = moduleObj.getInt("order")
            val title = moduleObj.getString("title")
            val category = moduleObj.optString("category", "")
            val readingTimeMinutes = moduleObj.optInt("readingTimeMinutes", 3)
            val themeColor = moduleObj.optString("themeColor", "rose")
            val heroDrawable = moduleObj.optString("heroDrawable", "")
            val heroTag = moduleObj.optString("heroTag", "")

            val sections = parseSections(moduleObj.optJSONArray("sections"))

            result.add(
                EducationModule(
                    id = id,
                    order = order,
                    title = title,
                    category = category,
                    readingTimeMinutes = readingTimeMinutes,
                    themeColor = themeColor,
                    heroDrawable = heroDrawable,
                    heroTag = heroTag,
                    sections = sections
                )
            )
        }

        return result.sortedBy { it.order }
    }

    private fun parseSections(sectionsArray: JSONArray?): List<EducationSection> {
        if (sectionsArray == null) return emptyList()
        val sections = mutableListOf<EducationSection>()

        for (i in 0 until sectionsArray.length()) {
            val sectionObj = sectionsArray.getJSONObject(i)
            val type = sectionObj.optString("type", "")

            when (type) {
                "overview" -> {
                    val title = sectionObj.optString("title", "")
                    val paragraphsArray = sectionObj.optJSONArray("paragraphs")
                    val paragraphs = mutableListOf<String>()
                    if (paragraphsArray != null) {
                        for (p in 0 until paragraphsArray.length()) {
                            paragraphs.add(paragraphsArray.getString(p))
                        }
                    }
                    sections.add(EducationSection.Overview(title, paragraphs))
                }

                "classification_list" -> {
                    val title = sectionObj.optString("title", "")
                    val standardBadge = sectionObj.optString("standardBadge", "")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<EducationSection.ClassificationItem>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            items.add(
                                EducationSection.ClassificationItem(
                                    title = itemObj.optString("title", ""),
                                    subtitle = itemObj.optString("subtitle", ""),
                                    badgeText = itemObj.optString("badgeText", ""),
                                    themeColor = itemObj.optString("themeColor", "emerald")
                                )
                            )
                        }
                    }
                    sections.add(EducationSection.ClassificationList(title, standardBadge, items))
                }

                "icon_list" -> {
                    val title = sectionObj.optString("title", "")
                    val subtitle = sectionObj.optString("subtitle", "")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<EducationSection.IconListItem>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            items.add(
                                EducationSection.IconListItem(
                                    iconType = itemObj.optString("iconType", "star"),
                                    title = itemObj.optString("title", ""),
                                    description = itemObj.optString("description", ""),
                                    themeColor = itemObj.optString("themeColor", "rose")
                                )
                            )
                        }
                    }
                    sections.add(EducationSection.IconList(title, subtitle, items))
                }

                "step_list" -> {
                    val title = sectionObj.optString("title", "")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<EducationSection.StepItem>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            items.add(
                                EducationSection.StepItem(
                                    number = itemObj.optInt("number", j + 1),
                                    title = itemObj.optString("title", ""),
                                    description = itemObj.optString("description", "")
                                )
                            )
                        }
                    }
                    sections.add(EducationSection.StepList(title, items))
                }

                "protocol_box" -> {
                    val title = sectionObj.optString("title", "")
                    val badgeText = sectionObj.optString("badgeText", "")
                    val header = sectionObj.optString("header", "")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<EducationSection.ProtocolItem>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            items.add(
                                EducationSection.ProtocolItem(
                                    title = itemObj.optString("title", ""),
                                    description = itemObj.optString("description", "")
                                )
                            )
                        }
                    }
                    sections.add(EducationSection.ProtocolBox(title, badgeText, header, items))
                }

                "warning_box" -> {
                    val title = sectionObj.optString("title", "")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<EducationSection.WarningItem>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            items.add(
                                EducationSection.WarningItem(
                                    title = itemObj.optString("title", ""),
                                    description = itemObj.optString("description", "")
                                )
                            )
                        }
                    }
                    sections.add(EducationSection.WarningBox(title, items))
                }

                "callout_box" -> {
                    val title = sectionObj.optString("title", "")
                    val message = sectionObj.optString("message", "")
                    val themeColor = sectionObj.optString("themeColor", "amber")
                    sections.add(EducationSection.CalloutBox(title, message, themeColor))
                }

                "screening_box" -> {
                    val title = sectionObj.optString("title", "")
                    val itemsArray = sectionObj.optJSONArray("items")
                    val items = mutableListOf<EducationSection.ScreeningItem>()
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            items.add(
                                EducationSection.ScreeningItem(
                                    title = itemObj.optString("title", ""),
                                    description = itemObj.optString("description", "")
                                )
                            )
                        }
                    }
                    sections.add(EducationSection.ScreeningBox(title, items))
                }
            }
        }

        return sections
    }
}
