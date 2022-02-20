package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ExceptClauseNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode?, symbol2: Token?, right: BaseNode?)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val leftNode = left
        val symbolTwo = symbol2
        val rightNode = right
}