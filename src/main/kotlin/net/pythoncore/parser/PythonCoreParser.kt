package net.pythoncore.parser

import net.pythoncore.parser.ast.*

class PythonCoreParser(scanner: PythonCoreTokenizer) {
    val tokenizer = scanner





    private fun parseAtom() : BaseNode {
        val start = tokenizer.curIndex
        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyFalse -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                return FalseLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyNone -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                return NoneLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyTrue -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                return TrueLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyElipsis -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                return ElipsisLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.NAME -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                return NameLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.NUMBER -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                return NumberLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.STRING -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.STRING) {
                    val nodes = mutableListOf<Token>()
                    nodes.add(symbol)
                    while (tokenizer.curSymbol.tokenKind == TokenCode.STRING) {
                        nodes.add(tokenizer.curSymbol)
                        tokenizer.advance()
                    }
                    return StringArrayLiteralNode(start, tokenizer.curIndex, nodes.toTypedArray())
                }
                return StringLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyLeftParen -> {
                throw NotImplementedError()
            }
            TokenCode.PyLeftBracket -> {
                throw NotImplementedError()
            }
            TokenCode.PyLeftCurly -> {
                throw NotImplementedError()
            }
            else -> {
                throw SyntaxError(tokenizer.curIndex, "Illegal literal!")
            }
        }
    }

    private fun parseAtomExpr() : BaseNode {
        val start = tokenizer.curIndex
        var symbol = Token(TokenCode.Empty)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyAwait) {
            symbol = tokenizer.curSymbol
            tokenizer.advance()
        }
        val node = parseAtom()
        if (tokenizer.curSymbol.tokenKind in setOf<TokenCode>(TokenCode.PyDot, TokenCode.PyLeftParen, TokenCode.PyLeftBracket)) {
            val nodes = mutableListOf<BaseNode>()
            while (tokenizer.curSymbol.tokenKind in setOf<TokenCode>(TokenCode.PyDot, TokenCode.PyLeftParen, TokenCode.PyLeftBracket)) {
                nodes.add(parseTrailer())
            }
            return AtomExpressionNode(start, tokenizer.curIndex, symbol.tokenKind == TokenCode.PyAwait, symbol, node, nodes.toTypedArray())
        }
        return AtomExpressionNode(start, tokenizer.curIndex, symbol.tokenKind == TokenCode.PyAwait, symbol, node, null )
    }

    private fun parsePower() : BaseNode {
        val start = tokenizer.curIndex
        val node = parseAtomExpr()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            return PowerOperatorNode(start, tokenizer.curIndex, node, symbol, parseFactor())
        }
        return node
    }

    fun parseFactor() : BaseNode {
        return BaseNode(-1, -1)
    }




    private fun parseTrailer() : BaseNode {
        return BaseNode(-1, -1)
    }
}