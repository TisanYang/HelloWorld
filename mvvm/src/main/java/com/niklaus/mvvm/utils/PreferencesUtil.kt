package com.niklaus.mvvm.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PreferencesUtil {

    private val mSharedPreferences: SharedPreferences by lazy {
        mContext.getSharedPreferences(CUSTOM_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private lateinit var mContext: Context

    fun init(context: Context) {
        this.mContext = context
    }

    /**
     * put string preferences .@param context
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     * storage.
     */
    fun putString(key: String, value: String): Boolean {
        val editor = mSharedPreferences.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    /**
     * get string preferences .@param context
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws
     * ClassCastException if there is a preference with this name that is
     * not a string
     * @see .getString
     */
    fun getString(key: String): String {
        return getString(key, "")
    }

    /**
     * get string preferences .@param context
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to common_icon_back if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is
     * not a string
     */
    fun getString(key: String, defaultValue: String): String {
        return mSharedPreferences.getString(key, defaultValue) ?: ""
    }

    /**
     * put int preferences .@param context
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     * storage.
     */
    fun putInt(key: String, value: Int): Boolean {
        val editor = mSharedPreferences.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    /**
     * get int preferences .@param context
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException
     * if there is a preference with this name that is not a int
     * @see .getInt
     */
    fun getInt(key: String): Int {
        return getInt(key, -1)
    }

    /**
     * get int preferences .@param context
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to common_icon_back if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is
     * not a int
     */
    fun getInt(key: String, defaultValue: Int): Int {
        try {
            return mSharedPreferences.getInt(key, defaultValue)
        } catch (e: Exception) {
            Log.e(CUSTOM_PREFERENCE_NAME, e.toString())
        }
        return defaultValue
    }

    /**
     * put long preferences .@param context
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     * storage.
     */
    fun putLong(key: String, value: Long): Boolean {
        val editor = mSharedPreferences.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    /**
     * get long preferences .@param context
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException
     * if there is a preference with this name that is not a long
     * @see .getLong
     */
    fun getLong(key: String): Long {
        return getLong(key, -1)
    }

    /**
     * get long preferences .@param context
     *
     * @param key The name of the preference to retrieve
     * @param defaultValue Value to common_icon_back if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is
     * not a long
     */
    fun getLong(key: String, defaultValue: Long): Long {
        try {
            return mSharedPreferences.getLong(key, defaultValue)
        } catch (e: Exception) {
            Log.e(CUSTOM_PREFERENCE_NAME, e.toString())
        }
        return defaultValue
    }

    /**
     * put float preferences .@param context
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     * storage.
     */
    fun putFloat(key: String, value: Float): Boolean {
        val editor = mSharedPreferences.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    /**
     * get float preferences .@param context
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException
     * if there is a preference with this name that is not a float
     * @see .getFloat
     */
    fun getFloat(key: String): Float {
        return getFloat(key, 0f)
    }

    /**
     * get float preferences .@param context
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to common_icon_back if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is
     * not a float
     */
    fun getFloat(key: String, defaultValue: Float): Float {
        try {
            return mSharedPreferences.getFloat(key, defaultValue)
        } catch (e: Exception) {
            Log.e(CUSTOM_PREFERENCE_NAME, e.toString())
        }
        return 0f
    }

    /**
     * put boolean preferences .@param context
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent
     * storage.
     */
    fun putBoolean(key: String, value: Boolean): Boolean {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    /**
     * get boolean preferences, default is false .@param context
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws
     * ClassCastException if there is a preference with this name that is
     * not a boolean
     * @see .getBoolean
     */
    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    /**
     * get boolean preferences .@param context
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to common_icon_back if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     * ClassCastException if there is a preference with this name that is
     * not a boolean
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * removeSharedPreferenceByKey:【remove obj in preferences】. <br></br>
     * .@param context .@param key .@common_icon_back.<br></br>
     */
    fun removeSharedPreferenceByKey(key: String): Boolean {
        val editor = mSharedPreferences.edit()
        editor.remove(key)
        return editor.commit()
    }

    /**
     * put int preferences .@param context
     *
     * @return True if the new values were successfully written to persistent
     * storage.
     */
    fun clean(): Boolean {
        val editor = mSharedPreferences.edit()
        editor.clear()
        return editor.commit()
    }

    fun remove(fileName: String, key: String): Boolean {
        val settings = mContext.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        val editor = settings.edit()
        editor.remove(key)
        return editor.commit()
    }

    companion object {
        val instance: PreferencesUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PreferencesUtil()
        }

        private const val CUSTOM_PREFERENCE_NAME = "car_pref"
    }
}