package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class DotNameNode(startPos: Int, endPos: Int, symbol: Token, name: Token)
    : BaseNode(startPos, endPos) {
        val dotNode = symbol
        val nameNode = name
}

class CallNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}

class IndexNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}