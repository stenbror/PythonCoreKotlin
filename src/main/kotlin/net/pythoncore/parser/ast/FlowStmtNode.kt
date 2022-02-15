package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class BreakStmtNode(startPos: Int, endPos: Int, symbol: Token) : EmptyBaseNode(startPos, endPos, symbol)

class ContinueStmtNode(startPos: Int, endPos: Int, symbol: Token) : EmptyBaseNode(startPos, endPos, symbol)

class ReturnStmtNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol, right)