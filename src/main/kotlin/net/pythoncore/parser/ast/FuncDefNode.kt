package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class FuncDefNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token, // def
    name: Token, // Name of function
    parameters: BaseNode, // ( ... )
    symbol2: Token?, // ->
    left: BaseNode?,
    symbol3: Token, // :
    typeComment: Token?,
    right: BaseNode // Suite
    )
    : BaseNode(startPos, endPos) {

        val symbolOne = symbol1 // 'def'
        val nameNode = name
        val parametersNode = parameters // '(' ... ')'
        val symbolTwo = symbol2// '->'
        val leftNode = left
        val symbolThree = symbol3 // ':'
        val typeCommentNode = typeComment
        val rightNode = right
}

class ParametersNode(startPos: Int, endPos: Int, symbol1: Token, node: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {

        val symbolOne = symbol1
        val rightNode = node
        val symbolTwo = symbol2
}