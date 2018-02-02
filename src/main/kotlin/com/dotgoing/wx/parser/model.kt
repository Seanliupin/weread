package com.dotgoing.wx.parser

import org.json.JSONObject

/**
 * 最为抽象的文本
 * */

interface Item {
    fun toJson(): JSONObject = JSONObject()
    fun isBreakLine() = false
    fun isParagraph() = false
    fun isQuote() = false
    fun isHead() = false
    fun mergeItem(item: Item) = this
    fun canMergeItem(item: Item) = false
    fun isBlock() = false
    fun isLi() = false
    fun isOrderLi() = false
    fun getSubItems() = listOf<Item>()
}

class Wired(val msg: String) : Item {
    override fun toJson(): JSONObject {
        throw InvalidItemError(msg)
    }
}

class InvalidItemError(msg: String) : Error("item 有问题: $msg")


data class Attrs(private val cls: String, private val style: String = "") {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("class", cls)
        json.put("style", style)
        return json
    }
}

/**
 * 最基础的文本片段，其可以在标题中，在正文中，在引文中等。
 * */
data class Text(val text: String, private val type: String = "text") : Item {
    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("text", text)
        json.put("type", "text")
        return json
    }
}

abstract class Node(private val name: String, private val attrs: Attrs, open val children: List<Item>) : Item {
    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("name", name)
        json.put("attrs", attrs.toJson())
        val processedChildren = procChildren()
        if (processedChildren.isNotEmpty()) {
            json.put("children", processedChildren.map { c -> c.toJson() })
        }
        return json
    }

    open fun procChildren(): List<Item> {
        return children
    }
}


/**
 * 标题
 * */
data class H(private val text: String, private val level: Int = 3, private val cls: String = "") : Node("h$level", Attrs("h$level $cls"), listOf(text.processLine())) {
    override fun isHead() = true
    override fun isBlock() = true
}

/**
 * 段落
 * */

data class P(private val items: List<Item> = listOf(), private val cls: String = "") : Node("p", Attrs("p $cls"), items) {
    override fun isParagraph() = true
}

/**
 * 换行
 * */
class Br : Node("br", Attrs("br"), listOf()) {
    override fun isBreakLine() = true
}

data class Ul(private val lis: List<Item>, private val ordered: Boolean = false, private val cls: String = "") : Node(if (ordered) "ol" else "ul", Attrs("ul $cls"), lis) {

    override fun canMergeItem(item: Item): Boolean {
        if (!item.isLi()) return false
        if (lis.isEmpty()) return true
        return lis[0].canMergeItem(item)
    }

    override fun mergeItem(item: Item): Item {
        if (!canMergeItem(item)) return this
        return Ul(lis + listOf(item), ordered, cls)
    }

    override fun isBlock() = true
}

data class NoOrderedLi(private val text: String, private val cls: String = "") : Node("li", Attrs("li $cls"), listOf(text.processLine())) {
    override fun isLi() = true

    override fun canMergeItem(item: Item): Boolean {
        if (!item.isLi() || item.isOrderLi()) return false
        return true
    }

    override fun mergeItem(item: Item): Item {
        if (!canMergeItem(item)) return this
        return Ul(listOf(this, item))
    }
}

data class OrderedLi(private val text: String, private val cls: String = "") : Node("li", Attrs("li $cls"), listOf(text.processLine())) {
    override fun isLi() = true

    override fun isOrderLi() = true

    override fun canMergeItem(item: Item): Boolean {
        if (!item.isLi() || !item.isOrderLi()) return false
        return true
    }

    override fun mergeItem(item: Item): Item {
        if (!canMergeItem(item)) return this
        return Ul(listOf(this, item), true)
    }
}

data class Quote(override val children: List<Item>, private val cls: String = "") : Node("div", Attrs("quote $cls"), children) {

    companion object {
        /**
         * 引文作为一个盒子模型，放入的任何文本都被当作段落处理
         * */
        fun simple(content: String): Quote {
            return (Quote(listOf(content.processLine())))
        }
    }

    override fun isQuote() = true

    override fun isBlock() = true

    override fun getSubItems(): List<Item> {
        return children
    }

    /**
     * 当最后两行是换行符时不可再加入进来，即结束掉quote部分
     * */
    override fun canMergeItem(item: Item): Boolean {
        if (children.size > 2 && children[children.size - 1].isBreakLine() && children[children.size - 2].isBreakLine()) {
            return false
        }
        return true
    }

    override fun mergeItem(item: Item): Item {
        if (!canMergeItem(item)) return this
        if (item.isQuote()) {
            //相邻的引用则合并之
            return Quote(children + item.getSubItems(), cls)
        }

        if (children.isEmpty()) return Quote(listOf(item), cls)
        val lastChild = children.last()

        //若新加进来的元素可以直接加到quote的尾元素上，则加之
        if (lastChild.canMergeItem(item)) {
            return Quote(children.dropLast(1) + listOf(lastChild.mergeItem(item)), cls)
        }

        return Quote(children + listOf(item), cls)
    }

    /**
     * 如果末尾有空行，则去掉
     * */
    private fun dropLastLineBreak(item: List<Item>): List<Item> {
        if (item.isEmpty()) return item

        return if (item.last().isBreakLine()) {
            dropLastLineBreak(item.dropLast(1))
        } else {
            item
        }
    }

    override fun procChildren(): List<Item> {
        return dropLastLineBreak(children)
    }

}

/**
 * 正文中强调部分。
 * */
data class Notice(private val text: String, private val cls: String = "notice") : Node("div", Attrs(cls), listOf(Text(text)))


data class Result(val ok: Boolean, val item: Item) {
    companion object {
        fun invalid(msg: String): Result {
            return Result(false, Wired(msg))
        }

        fun br(): Result {
            return Result(false, Br())
        }
    }
}

class Line(val text: String) {

    fun head(): Result {
        val match = Regex("(\\s*)(#+)([_.]?)(\\s+)(.*)").matchEntire(text) ?: return Result.invalid(text)

        val (_, sharp, bottom_line, _, content) = match.destructured
        //bottom_line 目前只有下划线(_)，还可以加上点划线(.)
        val h = H(content.trim(),
                if (sharp.length > 7) 7 else sharp.length,
                if (bottom_line == "_") "bottom_border" else "")
        return Result(true, h)
    }

    fun quoteBlock(): Result {
        val match = Regex("(\\s*)>(.*)").matchEntire(text) ?: return Result.invalid(text)
        val (_, content) = match.destructured

        return Result(true, Quote.simple(content))
    }


    fun li(): Result {

        fun orderLi(str: String): Result {
            val match = Regex("(\\s*)(\\d+)\\.(\\s+)(.*)").matchEntire(str) ?: return Result.invalid(str)
            val (_, _, _, content) = match.destructured
            return Result(true, OrderedLi(content))
        }

        fun noOrderLi(str: String): Result {
            val match = Regex("(\\s*)\\*(\\s+)(.*)").matchEntire(str) ?: return Result.invalid(str)
            val (_, _, content) = match.destructured
            return Result(true, NoOrderedLi(content))
        }

        val re = orderLi(text)

        return if (re.ok) {
            re
        } else {
            noOrderLi(text)
        }
    }
}


private fun String.processLine(): Item {
    val strongMarker = "_*"
    val spans = this.splitWithTag(strongMarker)

    /** 如果是空行，则将其看作是一个换行 **/
    if (spans.size == 1 && spans[0].isNormal() && spans[0].text.trim().isEmpty()) {
        return Br()
    }

    val items = spans.map { tag ->
        if (tag.isNormal()) {
            Text(tag.text)
        } else {
            Notice(tag.text)
        }
    }

    return P(items = items)
}

fun String.parse(): List<JSONObject> {
    val nodes = mutableListOf<Item>()

    this.split("\n")
            .map { line -> Line(line.slim()) }
            .forEach { line ->
                val choose = listOf(line.head(), line.li(), line.quoteBlock(), Result(true, line.text.processLine())).takeFirst { result -> result.ok }
                nodes.add(choose.item)
            }

    if (nodes.isEmpty()) return listOf()

    fun shrinkBreakLine(items: List<Item>): List<Item> {
        if (items.size < 3) return items

        return if (items[0].isBreakLine() && items[1].isBreakLine() && items[2].isBreakLine()) {
            shrinkBreakLine(items.drop(1))
        } else {
            listOf(items[0]) + shrinkBreakLine(items.drop(1))
        }
    }

    fun mergeAll(origin: List<Item>): List<Item> {
        if (origin.size < 2) return origin
        return if (origin[0].canMergeItem(origin[1])) {
            mergeAll(listOf(origin[0].mergeItem(origin[1])) + origin.drop(2))
        } else if (origin[0].isBlock() && origin[1].isBreakLine()) { // 块后面跟着换行符没有用
            listOf(origin[0]) + mergeAll(origin.drop(2))
        } else {
            listOf(origin[0]) + mergeAll(origin.drop(1))
        }
    }

    return shrinkBreakLine(mergeAll(nodes)).map { n -> n.toJson() }
}

/**---------------------- inner util helper ----------------------**/

/**
 * 这个列表必须要有元素，如果没有元素则报错。
 * */
private fun List<Result>.takeFirst(predicate: (t: Result) -> Boolean): Result {
    this.forEach { i ->
        if (predicate(i)) {
            return i
        }
    }
    return Result.br()
}

/**
 * @Deprecate 暂时没有用
 * */
@Deprecated("no-use")
private fun <R> String.indexedPair(sublen: Int = 2, transform: (index: Int, substring: String) -> R): List<R> {
    if (this.length < sublen) return listOf()
    val str = this
    val head = str.substring(0, str.length - sublen + 1)
    val tail = str.substring(sublen - 1, str.length)
    return head.zip(tail).mapIndexed { index, pair ->
        transform(index, "${pair.first}${pair.second}")
    }
}

/**
 * 去除连续空格，如果有连续大于两个空格，则将其压缩为两个空格.
 * */
private fun String.slim(): String {
    fun innerSlim(str: String): Pair<String, Boolean> {
        val newStr = str.replace("   ", "  ")
        return newStr to newStr.contentEquals(str)
    }

    var equal = false
    var original = this

    while (!equal) {
        val re = innerSlim(original)
        original = re.first
        if (re.second) equal = true
    }

    return original
}

abstract class Tag(open val text: String) {
    abstract fun isNormal(): Boolean
}

data class Normal(override val text: String) : Tag(text) {
    override fun isNormal(): Boolean {
        return true
    }
}

data class Inner(override val text: String) : Tag(text) {
    override fun isNormal(): Boolean {
        return false
    }
}


private fun String.findFirst(substring: String): Int {
    var index = -1
    var i = 0
    while (i < this.length) {
        if (this.substring(i).startsWith(substring)) return i
        i++
    }
    return index
}

private fun String.splitWithTag(tag: String): List<Tag> {
    val startIndex = this.findFirst(tag)
    val endIndex = this.findFirst(tag.reversed())

    if (startIndex >= endIndex) return listOf(Normal(this))

    return listOf(Normal(this.substring(0, startIndex)), Inner(this.substring(startIndex + tag.length, endIndex))) +
            this.substring(endIndex + tag.length).splitWithTag(tag)
}