package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class SubscriptNode(startPos: Int, endPos: Int, first: BaseNode, symbol1: Token, second: BaseNode, symbol2: Token, third: BaseNode)
    : TernaryNode(startPos, endPos, first, symbol1, second, symbol2, third)

