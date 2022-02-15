package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class PassStmtNode(startPos: Int, endPos: Int, symbol: Token )
    : EmptyBaseNode(startPos, endPos, symbol)