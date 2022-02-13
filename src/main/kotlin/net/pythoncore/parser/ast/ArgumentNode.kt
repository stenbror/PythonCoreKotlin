package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ArgumentNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)