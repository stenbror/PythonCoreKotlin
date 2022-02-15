package net.pythoncore.parser.ast

import net.pythoncore.parser.Token
import net.pythoncore.parser.TokenCode

class PlusAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class MinusAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class MulAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class PowerAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class DivAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class FloorDivAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class BitwiseOrAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class BitwiseXorAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class BitwiseAndAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class ShiftLeftAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class ShiftRightAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class MatriceAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class ModuloAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class AnnAssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode, symbol2: Token, next: BaseNode)
    : TernaryNode(startPos, endPos, left, symbol1, right, symbol2, next)

class AssignNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right) {
        var typeCommentNode = Token(TokenCode.Empty)

        fun addTypeCommentNode(symbol: Token) {
            typeCommentNode = symbol
        }
    }