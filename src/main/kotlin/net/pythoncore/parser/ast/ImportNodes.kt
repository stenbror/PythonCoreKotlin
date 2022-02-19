package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ImportNameNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol, right)

class ImportFromNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    left: BaseNode?,
    dots: Array<Token>,
    symbol2: Token,
    symbol3: Token?,
    right: BaseNode?,
    symbol4: Token?
    )
    : BaseNode(startPos, endPos) {

        val symbolOne = symbol1
        val leftNode = left
        val symbolTwo = symbol2
        val symbolThree = symbol3
        val rightNode = right
        val symbolFour = symbol4
}

class DottedNameNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>)
    : ListBaseNode(startPos, endPos, nodes, separators)

class ImportAsNamesNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>)
    : ListBaseNode(startPos, endPos, nodes, separators)

class DottedAsNamesNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>)
    : ListBaseNode(startPos, endPos, nodes, separators)

class DottedAsName(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class ImportAsName(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)