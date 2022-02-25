package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class VarArgAssignNodes(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

class VarArgsListNode(
    startPos: Int,
    endPos: Int,
    mulOp: Token?,
    mulNode: BaseNode?,
    powerOp: Token?,
    powerNode: BaseNode?,
    nodes: Array<BaseNode>?
)
    : BaseNode(startPos, endPos) {

        val mulOperator = mulOp
        val mulChildNode = mulNode
        val powerOperator = powerOp
        val powerChildNode = powerNode
        val elementNodes = nodes
}