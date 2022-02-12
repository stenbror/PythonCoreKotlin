package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class AtomExpressionNode(startPos: Int, endPos: Int, await: Boolean, symbol: Token, left: BaseNode, trailers: Array<BaseNode>?)
    : BaseNode(startPos, endPos) {
        val isAwait = await
        val symbolOne = symbol
        val leftNode = left
        val trailerNodes = trailers
}