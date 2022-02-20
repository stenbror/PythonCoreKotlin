package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class SuiteNode(startPos: Int, endPos: Int, symbol1: Token, symbol2: Token, nodes: Array<BaseNode>, symbol3: Token)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val symbolTwo = symbol2
        val elementNodes = nodes
        val symbolThree = symbol3
}