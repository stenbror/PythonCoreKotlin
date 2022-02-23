package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

open class SuiteNode(startPos: Int, endPos: Int, symbol1: Token, symbol2: Token, nodes: Array<BaseNode>, symbol3: Token)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val symbolTwo = symbol2
        val elementNodes = nodes
        val symbolThree = symbol3
}

class FuncSuiteNode(startPos: Int, endPos: Int, symbol1: Token, tc: Token?, newline: Token?, symbol2: Token, nodes: Array<BaseNode>, symbol3: Token)
    : SuiteNode(startPos, endPos, symbol1, symbol2, nodes, symbol3) {

        val typeCommentNode = tc
        val newlineAfterTypeCommentNode = newline
}