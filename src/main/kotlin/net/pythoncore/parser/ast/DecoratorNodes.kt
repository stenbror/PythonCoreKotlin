package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

// decorator: '@' dotted_name [ '(' [arglist] ')' ] NEWLINE
class DecoratorNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    left: BaseNode,
    symbol2: Token?,
    right: BaseNode?,
    symbol3: Token?,
    symbol4: Token
    )
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1 // @
        val leftNode = left // dotted_name
        val symbolTwo = symbol2 // (
        val rightNode = right // arglist
        val symbolThree = symbol3 // )
        val symbolFour = symbol4 // newline
}

class DecoratorsNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>)
    : BaseNode(startPos, endPos) {
        val elementsNode = nodes
}

class DecoratedNode(startPos: Int, endPos: Int, left: BaseNode, right: BaseNode)
    : BaseNode(startPos, endPos) {
        val leftNode = left // Decorators
        val rightNode = right // statement
}