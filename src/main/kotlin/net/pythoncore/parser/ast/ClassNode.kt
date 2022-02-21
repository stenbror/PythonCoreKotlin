package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class ClassNode(
    startPos: Int,
    endPos: Int,
    symbol1: Token,
    name: Token,
    symbol2: Token?,
    left: BaseNode?,
    symbol3: Token?,
    symbol4: Token,
    right: BaseNode
    )
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val nameElement = name
        val symbolTwo = symbol2
        val leftNode = right
        val symbolThree = symbol3
        val symbolFour = symbol4
        val rightNode = right
}