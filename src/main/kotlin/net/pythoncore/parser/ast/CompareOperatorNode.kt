package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class CompareLessOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareGreaterOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareEqualOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareNotEqualOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareLessEqualOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareGreaterEqualOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareInOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareIsOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol1, right)

class CompareNotInOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, symbol2: Token, right: BaseNode)
    : ExtendedBinaryNode(startPos, endPos, left, symbol1, symbol2, right)

class CompareIsNotOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, symbol2: Token, right: BaseNode)
    : ExtendedBinaryNode(startPos, endPos, left, symbol1, symbol2, right)