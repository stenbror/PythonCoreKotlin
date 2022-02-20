package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class WithStmtNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    nodes: Array<BaseNode>,
    separators: Array<Token>,
    symbol2: Token,
    typeComment: Token?,
    right: BaseNode)

    : ListBaseNode(startPos, endPos, nodes, separators) {
        val symbolOne = symbol1
        val symbolTwo = symbol1
        val typeCommentToken = typeComment
        val rightNode = right
}

class WithItemNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)