package net.pythoncore.parser.ast

import net.pythoncore.parser.Token
import javax.swing.JPopupMenu.Separator

class TupleNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
        val symbolOne = symbol1
        val rightNode = right
        val symbolTwo = symbol2
}

class ListNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}

class SetNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}

class DictionaryNode(startPos: Int, endPos: Int, symbol1: Token, right: BaseNode?, symbol2: Token)
    : BaseNode(startPos, endPos) {
    val symbolOne = symbol1
    val rightNode = right
    val symbolTwo = symbol2
}

class SetContainerNode(startPos: Int, endPos: Int, keys: Array<BaseNode>, separator: Array<Token>)
    : ListBaseNode(startPos, endPos, keys, separator)

class DictionaryContainerNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separator: Array<Token>)
    : ListBaseNode(startPos, endPos, nodes, separator)

class KeyValueNode(startPos: Int, endPos: Int, key: BaseNode, symbol: Token, value: BaseNode)
    : BinaryNode(startPos, endPos, key, symbol, value)
