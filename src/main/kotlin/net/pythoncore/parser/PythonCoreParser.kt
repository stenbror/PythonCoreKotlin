package net.pythoncore.parser

import net.pythoncore.parser.ast.*

class PythonCoreParser(scanner: PythonCoreTokenizer) {
    val tokenizer = scanner





    fun parseAtom() : BaseNode {
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

            else -> {
                throw SyntaxError(tokenizer.curIndex, "Illegal literal!")
            }
        }
    }
}