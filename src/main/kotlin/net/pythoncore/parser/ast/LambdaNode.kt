package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class LambdaNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode)
    : LambdaBaseNode(startPos, endPos, symbol1, left, symbol2, right)

class LambdaNoConditionalNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode)
    : LambdaBaseNode(startPos, endPos, symbol1, left, symbol2, right)