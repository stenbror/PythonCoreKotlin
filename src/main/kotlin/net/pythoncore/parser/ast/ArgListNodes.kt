package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TFPDefNode(startPos: Int, endPos: Int, left: BaseNode, symbol: Token, right: BaseNode)
    : BinaryNode(startPos, endPos, left, symbol, right)

