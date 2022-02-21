package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class SingleInputNode(startPos: Int, endPos: Int, symbol: Token, right: BaseNode)
    : UnaryNode(startPos, endPos, symbol, right)

class FileInputNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, newlines: Array<Token>, eof: Token)
    : ListBaseNode(startPos, endPos, nodes, newlines) {
        val eofNode = eof
}

class EvalInputNode(startPos: Int, endPos: Int, right: BaseNode, newlines: Array<Token>, eof: Token)
    : BaseNode(startPos, endPos) {
        val rightNode = right
        val newlineNode = newlines
        val eofNode = eof
}
