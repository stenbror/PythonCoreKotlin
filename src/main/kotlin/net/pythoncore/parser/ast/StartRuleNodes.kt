package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class SingleInputNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol, right)
