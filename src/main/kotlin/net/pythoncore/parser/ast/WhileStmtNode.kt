package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class WhileStmtNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode, next: BaseNode?)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val leftNode = left
        val symbolTwo = symbol2
        val rightNode = right
        val nextNode = next
}