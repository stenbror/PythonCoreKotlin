package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class FuncTypeNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode?, symbol2: Token, symbol3: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val leftNode = left
        val symbolTwo = symbol2
        val symbolThree = symbol3
        val rightNode = right
}

class FuncTypeInputNode(startPos: Int, endPos: Int, left: BaseNode, newlines: Array<Token>?, eof: Token)
    : BaseNode(startPos, endPos) {
        val leftNode = left
        val newlineNodes = newlines
        val symbolEof = eof
}