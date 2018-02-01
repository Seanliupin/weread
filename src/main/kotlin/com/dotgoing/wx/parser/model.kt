package com.dotgoing.wx.parser

import org.json.JSONObject

/**
 * 最为抽象的文本
 * */


interface Item {
    fun toJson(): JSONObject
}

data class Attrs(val cls: String, val style: String = "") {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("class", cls)
        json.put("style", style)
        return json
    }
}


abstract class Node(val name: String, val attrs: Attrs, open val children: List<Item>, val type: String = "node") : Item {
    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("name", name)
        json.put("attrs", attrs.toJson())
        val kkk = children.map { c -> c.toJson() }
        json.put("children", kkk)
        return json
    }
}

data class Text(val text: String, val type: String = "text") : Item {
    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("text", text)
        json.put("type", "text")
        return json
    }
}

data class H1(val text: String) : Node("h1", Attrs("head_1"), listOf(Text(text)))
data class H2(val text: String) : Node("h2", Attrs("head_2"), listOf(Text(text)))
data class H3(val text: String) : Node("h3", Attrs("head_3"), listOf(Text(text)))

data class P(val text: String) : Node("p", Attrs("head_3"), listOf(Text(text)))


data class Div(val cls: String, override val children: List<Item>) : Node("div", Attrs(cls), children)


fun String.parse(): List<JSONObject> {
    val h1 = "# "
    val h2 = "## "
    val h3 = "### "
    val nodes = mutableListOf<Item>()

    this.split("\n")
            .map { line -> line.trim() }
            .forEach { line ->

                when {
                    line.startsWith(h1) -> {
                        nodes.add(H1(line.replace(h1, "")))
                    }
                    line.startsWith(h2) -> {
                        nodes.add(H2(line.replace(h2, "")))
                    }
                    line.startsWith(h3) -> {
                        nodes.add(H3(line.replace(h3, "")))
                    }
                    else -> {

                    }
                }
            }

    return nodes.map { n -> n.toJson() }
}