package com.kangurusiaga.app.data.local

import android.content.Context
import com.kangurusiaga.app.domain.model.education.EducationModule
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EducationContentDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cachedModules: List<EducationModule>? = null

    fun getModules(): List<EducationModule> {
        cachedModules?.let { return it }

        val jsonString = context.assets.open("education/bblr_modules.json").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }
        val modules = EducationJsonParser.parse(jsonString)
        cachedModules = modules
        return modules
    }

    fun getModuleById(moduleId: String): EducationModule? {
        return getModules().firstOrNull { it.id == moduleId }
    }
}
