package com.denchic45.common

import com.denchic45.kts.util.getObjectValue
import kotlinx.serialization.json.Json
import org.junit.Test

class SampleTest {

    @Test
    fun testParser() {
        val json = """
        [
            {
                "document": {
                    "name": "projects/kts-app-2ab1f/databases/(default)/documents/Users/FadIyqp5sK2TQuTacJXI",
                    "fields": {
                        "firstName": {
                            "stringValue": "Денис"
                        }
                    },
                    "createTime": "2021-04-17T14:32:43.773288Z",
                    "updateTime": "2022-04-01T06:21:48.537627Z"
                },
                "readTime": "2022-07-27T13:12:33.258174Z"
            },
               {
                "document": {
                    "name": "projects/kts-app-2ab1f/databases/(default)/documents/Users/FadIyqp5sK2TQuTacJXI",
                    "fields": {
                        "firstName": {
                            "stringValue": "Иван"
                        }
                    },
                    "createTime": "2021-04-17T14:32:43.773288Z",
                    "updateTime": "2022-04-01T06:21:48.537627Z"
                },
                "readTime": "2022-07-27T13:12:33.258174Z"
            }

        ]

    """.trimIndent()

        println(
            """
            
            Object:
            
        """.trimIndent()
        )

        getObjectValue((Json.parseToJsonElement(json))).apply {
            (this as List<Map<String, Any>>).map {
                it.apply {
                    println(this.map { "${it.key}: ${it.value}" }.joinToString(", "))
                }
            }

//            val format = Json { prettyPrint = true }
//            println(format.encodeToJsonElement(this))
        }
    }
}