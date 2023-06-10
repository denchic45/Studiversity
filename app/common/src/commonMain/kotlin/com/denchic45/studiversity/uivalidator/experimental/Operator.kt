package com.denchic45.studiversity.uivalidator.experimental

import com.denchic45.studiversity.uivalidator.util.allEach
import com.denchic45.studiversity.uivalidator.util.anyEach

fun interface Operator {
    operator fun invoke(conditions: List<Condition<*>>): Boolean

    companion object {

        val AllEach: Operator = Operator { it.allEach(Condition<*>::validate) }

        val AnyEach: Operator = Operator { it.anyEach(Condition<*>::validate) }

        val All: Operator = Operator { it.all(Condition<*>::validate) }

        val Any: Operator = Operator { it.any(Condition<*>::validate) }
    }
}