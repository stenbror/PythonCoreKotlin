package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

open class BaseNode(startPos: Int, endPos: Int) {
    val nodeStartPos = startPos
    val nodeEndPos = endPos
}

open class UnaryNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
}

open class BinaryNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
    val leftNode = left
    val symbolOne = symbol1
    val rightNode = right

    override fun toString() : String {
        return """[CompareOperatorNode] 
                LeftNode: $leftNode
                Symbol: $symbolOne
                RightNode: $rightNode
            """.trimMargin()
    }
}

open class ExtendedBinaryNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, symbol2: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
    val leftNode = left
    val symbolOne = symbol1
    val symbolTwo = symbol2
    val rightNode = right

    override fun toString() : String {
        return """[CompareOperatorNode] 
                LeftNode: $leftNode
                Symbol1: $symbolOne
                Symbol2: $symbolTwo
                RightNode: $rightNode
            """.trimMargin()
    }
}