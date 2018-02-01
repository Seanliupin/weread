package com.dotgoing.wx.parser

/**
 * 最为抽象的文本
 * */


abstract class Item

abstract class Node(val name: String, val attrs: Attrs, open val children: List<Item>, val type: String = "node") : Item()

data class Text(val text: String, val type: String = "text") : Item()

data class Attrs(val cls: String, val style: String = "")

data class H1(val text: String) : Node("h1", Attrs("head_1"), listOf(Text(text)))
data class H2(val text: String) : Node("h2", Attrs("head_2"), listOf(Text(text)))
data class H3(val text: String) : Node("h3", Attrs("head_3"), listOf(Text(text)))

data class Div(val cls: String, override val children: List<Item>) : Node("div", Attrs(cls), children)


fun String.parse(): List<Item> {
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


    return nodes
}