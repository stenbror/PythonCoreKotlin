package net.pythoncore.parser.ast

import net.pythoncore.parser.Token

class TestListNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>?)
    : ListBaseNode(startPos, endPos, nodes, separators)

class ExprListNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>?)
    : ListBaseNode(startPos, endPos, nodes, separators)

class SubscriptListNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>?)
    : ListBaseNode(startPos, endPos, nodes, separators)

class ArgumentListNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>?)
    : ListBaseNode(startPos, endPos, nodes, separators)

class StatementListNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>?, newline: Token)
    : ListBaseNode(startPos, endPos, nodes, separators) {
        val newlineNode = newline
    }

class TestListStarExprNode(startPos: Int, endPos: Int, nodes: Array<BaseNode>, separators: Array<Token>?)
    : ListBaseNode(startPos, endPos, nodes, separators)