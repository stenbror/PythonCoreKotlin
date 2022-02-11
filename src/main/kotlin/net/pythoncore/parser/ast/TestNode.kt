package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TestNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode, symbol2: Token, next: BaseNode)
    : TernaryNode(startPos, endPos, left, symbol1, right, symbol2, next)

class OrTestNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class AndTestNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class NotTestNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol1, right)
