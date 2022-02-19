package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class GlobalStmtNode(startPos: Int, endPos: Int, symbol1: Token, nodes: Array<BaseNode>, separators: Array<Token>)
    : ListBaseNode(startPos, endPos, nodes, separators) {
    val symbolOne = symbol1
}

class NonlocalStmtNode(startPos: Int, endPos: Int, symbol1: Token, nodes: Array<BaseNode>, separators: Array<Token>)
    : ListBaseNode(startPos, endPos, nodes, separators) {
    val symbolOne = symbol1
}