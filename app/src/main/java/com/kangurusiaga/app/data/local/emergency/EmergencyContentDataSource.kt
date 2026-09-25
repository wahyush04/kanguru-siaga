package com.kangurusiaga.app.data.local.emergency

import android.content.Context
import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContentDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cachedModules: List<EmergencyWarningModule>? = null

    fun getModules(): List<EmergencyWarningModule> {
        cachedModules?.let { return it }

        val jsonString = context.assets.open("education/bblr_emergency_modules.json").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }
        val modules = EmergencyJsonParser.parse(jsonString)
        cachedModules = modules
        return modules
    }

    fun getModuleById(moduleId: String): EmergencyWarningModule? {
        return getModules().firstOrNull { it.id == moduleId }
    }
}
