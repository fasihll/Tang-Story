package com.example.tangstory

import com.example.tangstory.data.model.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "photo$i.com",
                i.toString(),
                "name $i",
                "description $i",
                i.toString(),
                i.toString(),
                i.toString(),
            )
            items.add(quote)
        }
        return items
    }
}