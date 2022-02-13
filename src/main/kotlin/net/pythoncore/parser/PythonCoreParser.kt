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

    private fun parseBitwiseOr() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseBitwiseXor()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyBitOr) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            left = BitwiseOrExpressionNode(start, tokenizer.curIndex, left, symbol, parseBitwiseXor())
        }
        return left
    }

    private fun parseStarExpr() : BaseNode {
        val start = tokenizer.curIndex
        val symbol = tokenizer.curSymbol
        assert(symbol.tokenKind == TokenCode.PyMul)
        tokenizer.advance()
        return BitwiseStarExpressionNode(start, tokenizer.curIndex, symbol, parseBitwiseOr())
    }

    private fun parseComparison() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseBitwiseOr()
        val operators = setOf(
                TokenCode.PyLess,
                TokenCode.PyLessEqual,
                TokenCode.PyEqual,
                TokenCode.PyGreater,
                TokenCode.PyGreaterEqual,
                TokenCode.PyNotEqual,
                TokenCode.PyNot,
                TokenCode.PyIn,
                TokenCode.PyIs)

        while (tokenizer.curSymbol.tokenKind in operators) {

            when (tokenizer.curSymbol.tokenKind) {
                TokenCode.PyLess -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareLessOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyLessEqual -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareLessEqualOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyEqual -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareEqualOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyNotEqual -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareNotEqualOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyGreaterEqual -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareGreaterEqualOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyGreater -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareGreaterOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyNot -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    if (tokenizer.curSymbol.tokenKind != TokenCode.PyIn) {
                        throw SyntaxError(tokenizer.curIndex, "Expecting 'not in', but missing 'in'")
                    }
                    val symbol2 = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareNotInOperatorNode(start, tokenizer.curIndex, left, symbol, symbol2, parseBitwiseOr())
                }
                TokenCode.PyIn -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    left = CompareInOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
                TokenCode.PyIs -> {
                    val symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    if (tokenizer.curSymbol.tokenKind == TokenCode.PyNot) {
                        val symbol2 = tokenizer.curSymbol
                        tokenizer.advance()
                        left = CompareIsNotOperatorNode(start, tokenizer.curIndex, left, symbol, symbol2, parseBitwiseOr())
                    }
                    left = CompareIsOperatorNode(start, tokenizer.curIndex, left, symbol, parseBitwiseOr())
                }
            }
        }
        return left;
    }

    private fun parseNotTest() : BaseNode {
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyNot) {
            val start = tokenizer.curIndex
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            return NotTestNode(start, tokenizer.curIndex, symbol, parseNotTest())
        }
        return parseComparison()
    }

    private fun parseAndTest() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseNotTest()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyAnd) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            left = AndTestNode(start, tokenizer.curIndex, left, symbol, parseNotTest())
        }
        return left
    }

    private fun parseOrTest() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseAndTest()

        while (tokenizer.curSymbol.tokenKind == TokenCode.PyOr) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            left = OrTestNode(start, tokenizer.curIndex, left, symbol, parseAndTest())
        }
        return left
    }

    private fun parseLambda(isCond: Boolean) : LambdaBaseNode {
        val start = tokenizer.curIndex
        val symbol = tokenizer.curSymbol
        assert(symbol.tokenKind == TokenCode.PyAssert)
        tokenizer.advance()
        var left = BaseNode(-1, -1)
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            // left = parseVarArgsList()
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in lambda expression!")
        }
        val symbol2 = tokenizer.curSymbol
        tokenizer.advance()
        if (isCond) {
            return LambdaNode(start, tokenizer.curIndex, symbol, left, symbol2, parseTest(true))
        }
        return LambdaNoConditionalNode(start, tokenizer.curIndex, symbol, left, symbol2, parseTest(false))
    }

    private fun parseTest(isCond: Boolean) : BaseNode {
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyLessEqual) {
            return parseLambda(isCond)
        }  else if (!isCond) {
            return parseOrTest()
        } else {
            val start = tokenizer.curIndex
            var left = parseOrTest()
            if (tokenizer.curSymbol.tokenKind == TokenCode.PyIf) {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val right = parseOrTest()
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyElse) {
                    throw SyntaxError(tokenizer.curIndex, "Expecting 'else' in expression!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                return TestNode(start, tokenizer.curIndex, left, symbol1, right, symbol2, parseTest(true))
            }
            return left
        }
    }

    private fun parseNamedExpr() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseTest(true)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyColonAssign) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            return ColonAssignNode(start, tokenizer.curIndex, left, symbol, parseTest(true))
        }
        return left
    }

    private fun parseTestListComp() : BaseNode {
        val start = tokenizer.curIndex
        val nodeFirst = if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseStarExpr() else parseNamedExpr()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyFor) {
            val nodes = mutableListOf<BaseNode>()
            nodes.add(nodeFirst)
            nodes.add(parseCompFor())
            return TestListNode(start, tokenizer.curIndex, nodes.toTypedArray(), null)
        } else if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            nodes.add(nodeFirst)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    throw SyntaxError(tokenizer.curIndex, "Unexpected ',' found in List!")
                } else if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyRightBracket, TokenCode.PyRightParen)) break
                nodes.add(if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseStarExpr() else parseNamedExpr())
            }
            return TestListNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return nodeFirst
    }

    private fun parseTrailer() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyDot, TokenCode.PyLeftParen, TokenCode.PyLeftBracket))
        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyDot -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                    throw SyntaxError(tokenizer.curIndex, "Missing NAME literal after '.'")
                }
                val name = tokenizer.curSymbol
                tokenizer.advance()
                return DotNameNode(start, tokenizer.curIndex, symbol, name)
            }
            TokenCode.PyLeftParen  -> {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                var node = BaseNode(-1, -1)
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) node = parseArgList()
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                    throw SyntaxError(tokenizer.curIndex, "Missing ')' in call expression!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                return CallNode(start, tokenizer.curIndex, symbol1, node, symbol2)
            }
            TokenCode.PyLeftBracket -> {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val node = parseSubscriptList()
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyLeftBracket) {
                    throw SyntaxError(tokenizer.curIndex, "Missing ']' in subscript!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                return IndexNode(start, tokenizer.curIndex, symbol1, node, symbol2)
            }
        }
        return BaseNode(-1, -1)
    }

    private fun parseSubscriptList() : BaseNode {
        val start = tokenizer.curIndex
        val nodeFirst = parseSubscript()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            nodes.add(nodeFirst)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    throw SyntaxError(tokenizer.curIndex, "Unexpected ',' found in List!")
                } else if (tokenizer.curSymbol.tokenKind == TokenCode.PyRightBracket) break
                nodes.add(parseSubscript())
            }
            return SubscriptListNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return nodeFirst
    }

    private fun parseSubscript() : BaseNode {
        val start = tokenizer.curIndex
        var first = BaseNode(-1, -1)
        var second = BaseNode(-1, -1)
        var third = BaseNode(-1, -1)
        var symbol1 = Token(TokenCode.Empty)
        var symbol2 = Token(TokenCode.Empty)
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) first = parseTest(true)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyColon) {
            symbol1 = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind !in setOf(TokenCode.PyColon, TokenCode.PyRightBracket, TokenCode.PyComma)) {
                second = parseTest(true)
            }
            if (tokenizer.curSymbol.tokenKind == TokenCode.PyColon) {
                symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind !in setOf(TokenCode.PyRightBracket, TokenCode.PyComma)) {
                    third = parseTest(true)
                }
            }
        }
        return SubscriptNode(start, tokenizer.curIndex, first, symbol1, second, symbol2, third)
    }

    private fun parseExprList() : BaseNode {
        val start = tokenizer.curIndex
        val nodeFirst = if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseStarExpr() else parseBitwiseOr()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            nodes.add(nodeFirst)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    throw SyntaxError(tokenizer.curIndex, "Unexpected ',' found in List!")
                } else if (tokenizer.curSymbol.tokenKind == TokenCode.PyIn) break
                nodes.add(if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseStarExpr() else parseBitwiseOr())
            }
            return ExprListNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return nodeFirst
    }

    private fun parseTestList() : BaseNode {
        val start = tokenizer.curIndex
        val nodeFirst = parseTest(true)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            nodes.add(nodeFirst)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    throw SyntaxError(tokenizer.curIndex, "Unexpected ',' found in List!")
                } else if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PySemiColon, TokenCode.Newline)) break
                nodes.add(parseTest(true))
            }
            return TestListNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return nodeFirst
    }

    private fun parseArgList() : BaseNode {
        val start = tokenizer.curIndex
        val nodeFirst = parseArgument()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            nodes.add(nodeFirst)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    throw SyntaxError(tokenizer.curIndex, "Unexpected ',' found in Argument List!")
                } else if (tokenizer.curSymbol.tokenKind == TokenCode.PyRightParen) break
                nodes.add(parseArgument())
            }
            return ArgumentListNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return nodeFirst
    }

    private fun parseArgument() : BaseNode {
        val start = tokenizer.curIndex
        var left = BaseNode(-1, -1)
        var symbol = Token(TokenCode.Empty)
        var right = BaseNode(-1, -1)
        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyMul -> {
                symbol = tokenizer.curSymbol
                tokenizer.advance()
                right = parseTest(true)
            }
            TokenCode.PyPower -> {
                symbol = tokenizer.curSymbol
                tokenizer.advance()
                right = parseTest(true)
            }
            TokenCode.NAME -> {
                val first = tokenizer.curSymbol
                tokenizer.advance()
                left = NameLiteralNode(start, tokenizer.curIndex, first)
                when (tokenizer.curSymbol.tokenKind) {
                    TokenCode.PyFor -> {
                        right = parseCompFor()
                    }
                    TokenCode.PyColonAssign -> {
                        symbol = tokenizer.curSymbol
                        tokenizer.advance()
                        right = parseTest(true)
                    }
                    TokenCode.PyAssign -> {
                        symbol = tokenizer.curSymbol
                        tokenizer.advance()
                        right = parseTest(true)
                    }
                    else -> {
                        return left
                    }
                }
            }
            else -> {
                throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal in argument!")
            }
        }
        return ArgumentNode(start, tokenizer.curIndex, left, symbol, right)
    }

    private fun parseDictorSetMaker() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseCompIter() : BaseNode {
        assert(tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyFor, TokenCode.PyAsync, TokenCode.PyIf))
        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyAsync -> {
                return parseCompFor()
            }
            TokenCode.PyFor -> {
                return parseCompFor()
            }
            TokenCode.PyIf -> {
                return parseCompIf()
            }
            else -> {
                throw NotImplementedError() // Should never happen due to assert statement,
            }
        }
    }

    private fun parseSyncCompFor() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseCompFor() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseCompIf() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseYieldExpr() : BaseNode {
        throw NotImplementedError()
    }
}