package com.denchic45.kts.data.repository

import android.util.Log
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.storage.MetaStorage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class MetaRepository @Inject constructor(
    private val appPreferences: AppPreferences,
    private val metaStorage: MetaStorage
) {
    private val oneMegabyte = (1024 * 1024).toLong()


    val lessonTime: Flow<Int> = appPreferences.observeLessonTime

    private suspend fun getMetaData() {
        metaStorage.get().let { meta ->
            appPreferences.lessonTime = meta.lesson_time
        }

        storage.getReference("meta.json")
            .getBytes(oneMegabyte)
            .addOnSuccessListener { bytes: ByteArray ->

            }
            .addOnFailureListener { e -> Log.d("lol", "onFailure: ", e) }
    }

    private fun tryGetInt(meta: JSONObject, key: String): Int {
        try {
            return meta.getInt(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun tryCreateJson(bytes: ByteArray): JSONObject {
        try {
            return JSONObject(String(bytes))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        throw IllegalStateException()
    }

    companion object {
        const val LESSON_TIME = "lesson_time"
    }

    init {
        getMetaData()
    }
}