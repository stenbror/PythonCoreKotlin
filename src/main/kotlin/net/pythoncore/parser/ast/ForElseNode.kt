package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ForStmtNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    left: BaseNode,
    symbol2: Token,
    right: BaseNode,
    symbol3: Token,
    symbol4: Token?,
    next: BaseNode,
    elsePart: BaseNode?
)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1 // for
        val leftNode = left
        val symbolTwo = symbol2 // in
        val rightNode = right
        val symbolThree = symbol3 // :
        val typeComment = symbol4
        val nextNode = next
        val elseNode = elsePart
}