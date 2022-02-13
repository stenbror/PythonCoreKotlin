package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class YieldNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val rightNode = right
}

class YieldFromNode(startPos: Int, endPos: Int, symbol1: Token, symbol2: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val symbolTwo = symbol2
        val rightNode = right
}