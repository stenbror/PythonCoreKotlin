package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TermMulOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class TermDivOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class TermModuloOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class TermMatriceOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class TermFloorDivOperatorNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)