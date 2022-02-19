package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class IfStmtNode(
        startPos: Int,
        endPos: Int,
        symbol1: Token,
        left: BaseNode,
        symbol2: Token,
        right: BaseNode,
        elifs: Array<BaseNode>?,
        elsePart: BaseNode?)
    : BaseNode(startPos, endPos) {
    
    val symbolOne = symbol1
    val leftNode = left
    val symbolTwo = symbol2
    val rightNode = right
    val elifNodes = elifs
    val elseNode = elsePart
}

class ElifStmtNode(
        startPos: Int,
        endPos: Int,
        symbol1: Token,
        left: BaseNode,
        symbol2: Token,
        right: BaseNode)
    : BaseNode(startPos, endPos) {

    val symbolOne = symbol1
    val leftNode = left
    val symbolTwo = symbol2
    val rightNode = right
    }

class ElseStmtNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    symbol2: Token,
    right: BaseNode)
    : BaseNode(startPos, endPos) {

    val symbolOne = symbol1
    val symbolTwo = symbol2
    val rightNode = right
}