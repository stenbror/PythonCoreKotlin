package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class BreakStmtNode(startPos: Int, endPos: Int, symbol: Token) : EmptyBaseNode(startPos, endPos, symbol)

class ContinueStmtNode(startPos: Int, endPos: Int, symbol: Token) : EmptyBaseNode(startPos, endPos, symbol)

class ReturnStmtNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol, right)

class RaiseStmtNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode)
    :   BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val leftNode = left
        val symbolTwo = symbol2
        val rightNode = right
    }