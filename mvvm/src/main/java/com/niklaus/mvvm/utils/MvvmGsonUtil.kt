package com.niklaus.mvvm.utils

import android.content.Context
import android.text.TextUtils
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type

/**
 * gson解析工具类
 */
object MvvmGsonUtil {

    private val gson = Gson()

    /**
     * 特殊字符的转义(解析内容包含了特殊字符)
     */
    private val gson2 = GsonBuilder().disableHtmlEscaping().create()

    /**
     * json字符串转成对象
     *
     * @param str  json字符串
     * @param type 目标类.class
     * List<Person> rtn = GsonUtil.fromJson(jsonStr, new TypeToken<List></List><Person>>(){}.getType());
     * @return
    </Person></Person> */
    fun <T> fromJson(str: String?, type: Type?): T {
        return gson.fromJson(str, type)
    }

    /**
     * json字符串转成对象
     *
     * @param str   Json字符串
     * @param clazz 转换对象的类型
     * Person newPerson = GsonUtil.fromJson(jsonStr, Person.class);
     * @return T类型对象
     */
    fun <T> fromJson(str: String?, clazz: Class<T>?): T {
        return gson.fromJson(str, clazz)
    }

    fun <T> fromJson(jsonElement: JsonElement?, type: Class<T>?): T {
        return gson.fromJson(jsonElement, type)
    }

    fun <T> listFromJson(data: String?, clazz: Class<T>?): List<T>? {
        return try {
            val type = TypeToken.getParameterized(MutableList::class.java, clazz).type
            gson.fromJson(data, type)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    fun toJson(src: Any?): String {
        return gson.toJson(src)
    }

    fun toJson2(src: Any?): String {
        return gson2.toJson(src)
    }

    fun toJson(src: Any?, root: String?): String {
        val json = JsonObject()
        json.add(root, gson.toJsonTree(src))
        return json.toString()
    }

    fun <T> fromMapJson(json: String?, clazz: Class<T>?): Map<String, T> {
        val type =
            TypeToken.getParameterized(MutableMap::class.java, String::class.java, clazz).type
        return gson.fromJson(json, type)
    }

    fun <T> fromMapListJson(json: String?, clazz: Class<T>?): List<Map<String, T>> {
        val type = TypeToken.getParameterized(
            MutableList::class.java,
            MutableMap::class.java,
            String::class.java,
            clazz
        ).type
        return gson.fromJson(json, type)
    }

    fun <T> readJsonFile(
        context: Context,
        relativePathofFileUnderAsset: String?,
        clazz: Class<T>?
    ): T? {
        var reader: JsonReader? = null
        var result: T? = null
        try {
            reader = JsonReader(
                InputStreamReader(
                    context.assets.open(relativePathofFileUnderAsset!!), "UTF-8"
                )
            )
            result = gson.fromJson(reader, clazz)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    fun <T> readJsonFiletoList(context: Context, filename: String?, clazz: Class<T>?): List<T>? {
        var reader: JsonReader? = null
        try {
            reader = JsonReader(
                InputStreamReader(
                    context.assets.open(filename!!), "UTF-8"
                )
            )
            val type = TypeToken.getParameterized(MutableList::class.java, clazz).type
            return gson.fromJson(reader, type)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    const val JSONARRAY = 1
    const val JSONOBJECT = 0
    const val INVALIDJSON = -1

    /**
     * 判断Json字符串是数组([{...}])还是Object( {...})
     *
     * @param data json 字符串
     * @return 类型；
     *
     *  *  -1；无效json 字符串；
     *  *  0：Object；
     *  *  1：Array;
     *
     * created by Bryan.xie on 20190220
     */
    fun isJsonArrayOrObject(data: String?): Int {
        if (TextUtils.isEmpty(data)) {
            return INVALIDJSON
        }
        val json: Any
        try {
            json = JSONTokener(data).nextValue()
            if (json is JSONObject) {
                //you have an object
                return JSONOBJECT
            } else if (json is JSONArray) {
                //you have an array
                return JSONARRAY
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return INVALIDJSON
        }
        return INVALIDJSON
    }

    /**
     * Gson 解析排除指定类策略
     */
    class SpecificClassExclusionStrategy(private val excludedThisClass: Class<*>) :
        ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return excludedThisClass == clazz
        }

        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return excludedThisClass == f.declaredClass
        }
    }
}