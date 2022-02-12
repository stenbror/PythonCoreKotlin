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

    override fun toString() : String {
        return """[UnaryNode] 
                Symbol: $symbolOne
                RightNode: $rightNode
            """.trimMargin()
    }
}

open class BinaryNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
    val leftNode = left
    val symbolOne = symbol1
    val rightNode = right

    override fun toString() : String {
        return """[BinaryNode] 
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
        return """[ExtendedBinaryNode] 
                LeftNode: $leftNode
                Symbol1: $symbolOne
                Symbol2: $symbolTwo
                RightNode: $rightNode
            """.trimMargin()
    }
}

open class TernaryNode(startPos: Int, endPos: Int, left: BaseNode, symbol1: Token, right: BaseNode, symbol2: Token, next: BaseNode)
    : BaseNode(startPos, endPos) {
    val leftNode = left
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
    val nextNode = next

    override fun toString() : String {
        return """[TernaryNode] 
                LeftNode: $leftNode
                Symbol1: $symbolOne
                RightNode: $rightNode
                Symbol2: $symbolTwo
                NextNode: $nextNode
            """.trimMargin()
    }
}

open class LambdaBaseNode(startPos: Int, endPos: Int, symbol1: Token, left: BaseNode, symbol2: Token, right: BaseNode)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val leftNode = left
    val symbolTwo = symbol2
    val rightNode = right

    override fun toString() : String {
        return """[LambdaNode] 
                Symbol1: $symbolOne
                LeftNode: $leftNode
                Symbol2: $symbolTwo
                RightNode: $rightNode
            """.trimMargin()
    }
}

open class LiteralBaseNode(startPos: Int, endPos: Int, symbol1: Token?)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1

    override fun toString() : String {
        return """[LiteralNode] 
                Symbol1: $symbolOne
            """.trimMargin()
    }
}
