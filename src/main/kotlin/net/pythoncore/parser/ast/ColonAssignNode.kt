package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ColonAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)