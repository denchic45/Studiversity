package com.denchic45.studiversity.widget.extendedAdapter

interface IAdapter {

    val adapterEventEmitter: ObserverEventEmitter

    val listItems: MutableList<Any>

    val count : Int
        get() = listItems.size
}