package net.pythoncore.parser

import net.pythoncore.parser.ast.*

class PythonCoreParser(scanner: PythonCoreTokenizer) {
    private val tokenizer = scanner





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
        if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyDot, TokenCode.PyLeftParen, TokenCode.PyLeftBracket)) {
            val nodes = mutableListOf<BaseNode>()
            while (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyDot, TokenCode.PyLeftParen, TokenCode.PyLeftBracket)) {
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

    private fun parseFactor() : BaseNode {
        val start = tokenizer.curIndex
        val symbol = tokenizer.curSymbol
        when (symbol.tokenKind) {
            TokenCode.PyPlus -> {
                tokenizer.advance()
                return FactorUnaryPlusNode(start, tokenizer.curIndex, symbol, parseFactor())
            }
            TokenCode.PyMinus -> {
                tokenizer.advance()
                return FactorUnaryMinusNode(start, tokenizer.curIndex, symbol, parseFactor())
            }
            TokenCode.PyBitInvert -> {
                tokenizer.advance()
                return FactorUnaryInvertNode(start, tokenizer.curIndex, symbol, parseFactor())
            }
            else -> {
                return parsePower()
            }
        }
    }

    private fun parseTerm() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseFactor()

        while (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyMul, TokenCode.PyDiv, TokenCode.PyFloorDiv, TokenCode.PyModulo, TokenCode.PyMatrice)) {

            when (tokenizer.curSymbol.tokenKind) {
                TokenCode.PyMul -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = TermMulOperatorNode(start, tokenizer.curIndex, left, symbol, parseFactor())
                }
                TokenCode.PyDiv -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = TermDivOperatorNode(start, tokenizer.curIndex, left, symbol, parseFactor())
                }
                TokenCode.PyFloorDiv -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = TermFloorDivOperatorNode(start, tokenizer.curIndex, left, symbol, parseFactor())
                }
                TokenCode.PyModulo -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = TermModuloOperatorNode(start, tokenizer.curIndex, left, symbol, parseFactor())
                }
                TokenCode.PyMatrice -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = TermMatriceOperatorNode(start, tokenizer.curIndex, left, symbol, parseFactor())
                }
            }
        }
        return left
    }

    private fun parseArith() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseTerm()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyPlus || tokenizer.curSymbol.tokenKind == TokenCode.PyMinus) {

            when (tokenizer.curSymbol.tokenKind) {
                TokenCode.PyPlus -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = ArithmeticPlusOperatorNode(start, tokenizer.curIndex, left, symbol, parseTerm())
                }
                TokenCode.PyMinus -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = ArithmeticMinusOperatorNode(start, tokenizer.curIndex, left, symbol, parseTerm())
                }
            }
        }
        return left
    }

    private fun parseShift() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseArith()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyShiftLeft || tokenizer.curSymbol.tokenKind == TokenCode.PyShiftRight) {

            when (tokenizer.curSymbol.tokenKind) {
                TokenCode.PyShiftLeft -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = BitwiseShiftLeftExpressionNode(start, tokenizer.curIndex, left, symbol, parseArith())
                }
                TokenCode.PyShiftRight -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = BitwiseShiftRightExpressionNode(start, tokenizer.curIndex, left, symbol, parseArith())
                }
            }
        }
        return left
    }

    private fun parseBitwiseAnd() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseShift()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyBitAnd) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            left = BitwiseAndExpressionNode(start, tokenizer.curIndex, left, symbol, parseShift())
        }
        return left
    }

    private fun parseBitwiseXor() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseBitwiseAnd()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyBitXor) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            left = BitwiseXorExpressionNode(start, tokenizer.curIndex, left, symbol, parseBitwiseAnd())
        }
        return left
    }


    private fun parseTrailer() : BaseNode {
        return BaseNode(-1, -1)
    }
}