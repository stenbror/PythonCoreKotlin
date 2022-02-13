package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class CompForNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : BaseNode (startPos, endPos) {
        val symbolOne = symbol
        val rightNode = right
    }

class CompSyncForNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode, next: BaseNode?)
    : BaseNode (startPos, endPos) {
    val symbolOne = symbol1
    val leftNode = left
    val symbolTwo = symbol2
    val rightNode = right
    val nextNode = next
}

class CompIfNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, next: BaseNode?)
    : BaseNode (startPos, endPos) {
    val symbolOne = symbol1
    val leftNode = left
    val nextNode = next
}