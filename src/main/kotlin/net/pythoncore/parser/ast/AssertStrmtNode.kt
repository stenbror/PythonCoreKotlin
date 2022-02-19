package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class AssertStmtNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol2, right) {
    val symbolFirst = symbol1
}