package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class NameLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

class NumberLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

class StringLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

class StringArrayLiteralNode(startPos: Int, endPos: Int, symbols: Array<Token>) : LiteralBaseNode(startPos, endPos, null) {
    val symbolNodes = symbols
}

class FalseLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

class NoneLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

class TrueLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

class ElipsisLiteralNode(startPos: Int, endPos: Int, symbol: Token) : LiteralBaseNode(startPos, endPos, symbol)

