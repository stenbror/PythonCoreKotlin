package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TupleNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val rightNode = right
        val symbolTwo = symbol2
}

class ListNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}

class SetNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}

class DictionaryNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}