package dev.aurakai.auraframefx

import android.content.Context
import android.content.SharedPreferences

/**
 * 🛠️ YukiHookModulePrefs - Lightweight Preferences Wrapper
 *
 * This provides a consistent interface for module preferences,
 * abstracted from the underlying storage (SharedPreferences for now).
 */
class YukiHookModulePrefs private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        @Volatile
        private var instance: YukiHookModulePrefs? = null

        fun from(context: Context): YukiHookModulePrefs {
            return instance ?: synchronized(this) {
                instance ?: YukiHookModulePrefs(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()

    fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)
    fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()

    fun getFloat(key: String, defaultValue: Float): Float = prefs.getFloat(key, defaultValue)
    fun putFloat(key: String, value: Float) = prefs.edit().putFloat(key, value).apply()

    fun getString(key: String, defaultValue: String?): String? = prefs.getString(key, defaultValue)
    fun putString(key: String, value: String?) = prefs.edit().putString(key, value).apply()
}
