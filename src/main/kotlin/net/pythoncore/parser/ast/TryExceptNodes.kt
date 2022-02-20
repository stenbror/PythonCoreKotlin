package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ExceptClauseNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode?, symbol2: Token?, right: BaseNode?)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val leftNode = left
        val symbolTwo = symbol2
        val rightNode = right
}

class ExceptStmtNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class TryStmtNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    symbol2: Token,
    left: BaseNode,
    excepts: Array<BaseNode>?,
    elsePart: BaseNode?,
    finally: BaseNode?)

    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val symbolTwo = symbol2
        val leftNode = left
        val exceptsNode = excepts
        val elseNode = elsePart
        val finallyNode = finally
}

class FinallyStmtNode(startPos: Int, endPos: Int, symbol1: Token, symbol2: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val symbolTwo = symbol2
        val rightNode = right
    }