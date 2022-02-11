package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TestNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode, symbol2: Token, next: BaseNode)
    : TernaryNode(startPos, endPos, left, symbol1, right, symbol2, next)