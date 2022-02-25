package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TypedArgAssignNodes(startPos: Int, endPos: Int, typeComment: Token?, left: BaseNode, symbol: Token?, right: BaseNode?)
    : BaseNode(startPos, endPos) {
        val typeCommentNode = typeComment
        val leftNode = left
        val symbolOne = symbol
        val rightNode = right
    }

class TypedArgsListNode(
    startPos: Int,
    endPos: Int,
    mulOp: Token?,
    mulNode: BaseNode?,
    powerOp: Token?,
    powerNode: BaseNode?,
    slashOp: Token?,
    nodes: Array<BaseNode>?,
    separators: Array<Token>?
)
    : BaseNode(startPos, endPos) {

    val mulOperator = mulOp
    val mulChildNode = mulNode
    val powerOperator = powerOp
    val powerChildNode = powerNode
    val slashOperator = slashOp
    val elementNodes = nodes
    val separatorsOperator = separators
}