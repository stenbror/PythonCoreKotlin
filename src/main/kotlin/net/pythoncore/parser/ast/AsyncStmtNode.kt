package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class AsyncStmtNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol, right)
