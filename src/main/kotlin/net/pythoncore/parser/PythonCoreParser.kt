package net.pythoncore.parser

import net.pythoncore.parser.ast.*

class PythonCoreParser(scanner: PythonCoreTokenizer) {
    private val tokenizer = scanner
    private var level = 0 // Loop statement that break and continue can handle
    private var funcLevel = 0 // function allows return statement

    // Block rules below!

    fun parseSingleInput() : BaseNode {
        tokenizer.advance()
        val start = tokenizer.curIndex
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.Newline -> {
                val newline = tokenizer.curSymbol
                tokenizer.advance()
                SingleInputNode(start, tokenizer.curIndex, newline, BaseNode(-1, -1))
            }
            TokenCode.PyIf,
            TokenCode.PyWhile,
            TokenCode.PyFor,
            TokenCode.PyTry,
            TokenCode.PyWith,
            TokenCode.PyDef,
            TokenCode.PyClass,
            TokenCode.PyMatrice,
            TokenCode.PyAsync -> {
                val right = parseCompoundStmt()
                if (tokenizer.curSymbol.tokenKind != TokenCode.Newline) {
                    throw SyntaxError(tokenizer.curIndex, "Missing NEWLINE after statement!")
                }
                val newline = tokenizer.curSymbol
                tokenizer.advance()
                SingleInputNode(start, tokenizer.curIndex, newline, right)
            }
            else -> {
                val right = parseSimpleStmt()
                SingleInputNode(start, tokenizer.curIndex, Token(TokenCode.Empty), right)
            }
        }
    }

    fun parseFileInput() : BaseNode {
        tokenizer.advance()
        val start = tokenizer.curIndex
        val nodes = mutableListOf<BaseNode>()
        val newlines = mutableListOf<Token>()
        while (tokenizer.curSymbol.tokenKind != TokenCode.EOF) {
            if (tokenizer.curSymbol.tokenKind == TokenCode.Newline) {
                newlines.add(tokenizer.curSymbol)
                tokenizer.advance()
            }
            else {
                nodes.add(parseStmt())
            }
        }
        return FileInputNode(start, tokenizer.curIndex, nodes.toTypedArray(), newlines.toTypedArray(), tokenizer.curSymbol)
    }

    fun parseEvalInput() : BaseNode {
        tokenizer.advance()
        val start = tokenizer.curIndex
        val newlines = mutableListOf<Token>()
        val right = parseTestList()
        while (tokenizer.curSymbol.tokenKind == TokenCode.Newline) {
            newlines.add(tokenizer.curSymbol)
            tokenizer.advance()
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.EOF) {
            throw SyntaxError(tokenizer.curIndex, "Expecting EOF!")
        }
        return EvalInputNode(start, tokenizer.curIndex, right, newlines.toTypedArray(), tokenizer.curSymbol)
    }

    fun parseFuncTypeInput() : BaseNode {
        tokenizer.advance()
        val start = tokenizer.curIndex
        val left = parseFuncType()
        val newlines = mutableListOf<Token>()
        while (tokenizer.curSymbol.tokenKind == TokenCode.Newline) {
            newlines.add(tokenizer.curSymbol)
            tokenizer.advance()
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.EOF) {
            throw SyntaxError(tokenizer.curIndex, "Expecting end of file!")
        }
        return FuncTypeInputNode(start, tokenizer.curIndex, left, if (newlines.isEmpty()) null else  newlines.toTypedArray(), tokenizer.curSymbol)
    }

    private fun parseDecorator() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyMatrice)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val left = parseDottedName()
        var symbol2: Token? = null
        var right: BaseNode? = null
        var symbol3: Token? = null
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyLeftParen) {
            symbol2 = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                right = parseArgList()
            }
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                throw SyntaxError(tokenizer.curIndex, "Expecting ')' in decorator!")
            }
            symbol3 = tokenizer.curSymbol
            tokenizer.advance()
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.Newline) {
            throw SyntaxError(tokenizer.curIndex, "Expecting NEWLINE in decorator!")
        }
        val symbol4 = tokenizer.curSymbol
        tokenizer.advance()
        return DecoratorNode(start, tokenizer.curIndex, symbol1, left, symbol2, right, symbol3, symbol4)
    }

    private fun parseDecorators() : BaseNode {
        val start = tokenizer.curIndex
        val nodes = mutableListOf<BaseNode>()
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyMatrice) {
            nodes.add(parseDecorator())
        }
        return DecoratorsNode(start, tokenizer.curIndex, nodes.toTypedArray())
    }

    private fun parseDecorated() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyMatrice)
        val left = parseDecorators()
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyClass -> {
                val right = parseClassDef()
                DecoratedNode(start, tokenizer.curIndex, left, right)
            }
            TokenCode.PyDef -> {
                val right = parseFuncDef()
                DecoratedNode(start, tokenizer.curIndex, left, right)
            }
            TokenCode.PyAsync -> {
                val right = parseAsyncFuncDef()
                DecoratedNode(start, tokenizer.curIndex, left, right)
            }
            else -> {
                throw SyntaxError(tokenizer.curIndex, "Expecting 'class', 'def' or 'async' after decorators!")
            }
        }
    }

    private fun parseClassDef() : BaseNode {
        // classdef: 'class' NAME ['(' [arglist] ')'] ':' suite
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyClass)
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting NAME of 'class' declaration!")
        }
        val name = tokenizer.curSymbol
        tokenizer.advance()
        var symbol2: Token? = null
        var left: BaseNode? = null
        var symbol3: Token? = null
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyLeftParen) {
            symbol2 = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                left = parseArgList()
            }
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                throw SyntaxError(tokenizer.curIndex, "Expecting ')' in 'class' declaration!")
            }
            symbol3 = tokenizer.curSymbol
            tokenizer.advance()
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'class' dec larationb!")
        }
        val symbol4 = tokenizer.curSymbol   // :
        tokenizer.advance()
        val right = parseSuite()
        return ClassNode(start, tokenizer.curIndex, symbol, name, symbol2, left, symbol3, symbol4, right)
    }

    private fun parseAsyncFuncDef() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyAsync)
        var symbol = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyDef) {
            throw SyntaxError(tokenizer.curIndex, "Expecting 'def' after 'async' in function declaration!")
        }
        val right = parseFuncDef()
        return AsyncStmtNode(start, tokenizer.curIndex, symbol, right)
    }

    private fun parseFuncDef() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyDef)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Missing name of function!")
        }
        val name = tokenizer.curSymbol
        tokenizer.advance()
        val para = parseParameters()
        var symbol2: Token? = null
        var left: BaseNode? = null
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyArrow) {
            symbol2 = tokenizer.curSymbol
            tokenizer.advance()
            left = parseTest(true)
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in function declaration!")
        }
        val symbol3 = tokenizer.curSymbol
        tokenizer.advance()
        var typeCom: Token? = null
        if (tokenizer.curSymbol.tokenKind == TokenCode.TypeComment) {
            typeCom = tokenizer.curSymbol
            tokenizer.advance()
        }
        val right = parseFuncBodySuite()
        return FuncDefNode(start, tokenizer.curIndex, symbol1, name, para, symbol2, left, symbol3, typeCom, right)
    }

    private fun parseParameters() : BaseNode {
        val start = tokenizer.curIndex
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyLeftParen) {
            throw SyntaxError(tokenizer.curIndex, "Expecting '(' in parameters of function declaration!")
        }
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val right = if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) parseTypedArgsList() else null
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ')' in parameters of function declaration!")
        }
        val symbol2 = tokenizer.curSymbol
        tokenizer.advance()
        return ParametersNode(start, tokenizer.curIndex, symbol1, right, symbol2)
    }

    private fun parseTypedArgsList() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseTFPDef() : BaseNode {
        val start = tokenizer.curIndex
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting literal Name in argument!")
        }
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        val left = NameLiteralNode(start, tokenizer.curIndex, symbol)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyColon) {
            val symbolColon = tokenizer.curSymbol
            tokenizer.advance()
            val right = parseTest(true)
            return TFPDefNode(start, tokenizer.curIndex, left, symbolColon, right)
        }
        return left
    }

    private fun parseVarArgsList() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseVFPDef() : BaseNode {
        val start = tokenizer.curIndex
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting literal Name in argument!")
        }
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        return NameLiteralNode(start, tokenizer.curIndex, symbol)
    }

    // Statement rules below!

    private fun parseStmt() : BaseNode {
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyIf,
            TokenCode.PyFor,
            TokenCode.PyWhile,
            TokenCode.PyTry,
            TokenCode.PyWith,
            TokenCode.PyAsync,
            TokenCode.PyDef,
            TokenCode.PyClass,
            TokenCode.PyMatrice -> {
                parseCompoundStmt()
            }
            else -> {
                parseSimpleStmt()
            }
        }
    }

    private fun parseSimpleStmt() : BaseNode {
        val start = tokenizer.curIndex
        val firstNode = parseSmallStmt()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PySemiColon) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            var symbol = Token(TokenCode.Empty)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PySemiColon) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind == TokenCode.Newline) {
                    break
                }
                nodes.add(parseSmallStmt())
            }
            if (tokenizer.curSymbol.tokenKind != TokenCode.Newline) {
                throw SyntaxError(tokenizer.curIndex, "Expecting NEWLINE after statement list!")
            }
            symbol = tokenizer.curSymbol
            tokenizer.advance()
            return StatementListNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray(), symbol)
        }
        return firstNode
    }

    private fun parseSmallStmt() : BaseNode {
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyDel -> parseDelStmt()
            TokenCode.PyPass -> parsePassStmt()
            TokenCode.PyBreak,
            TokenCode.PyContinue,
            TokenCode.PyReturn,
            TokenCode.PyRaise,
            TokenCode.PyYield -> parseFlowStmt()
            TokenCode.PyImport,
            TokenCode.PyFrom -> parseImportStmt()
            TokenCode.PyGlobal -> parseGlobalStmt()
            TokenCode.PyNonlocal -> parseNonlocalStmt()
            TokenCode.PyAssert -> parseAssertStmt()
            else -> {
                parseExprStmt()
            }
        }
    }

    private fun parseExprStmt() : BaseNode {
        val start = tokenizer.curIndex
        var left = parseTestListStarExpr()
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyPlusAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                PlusAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyMinusAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                MinusAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyMulAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                MulAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyPowerAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                PowerAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyDivAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                DivAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyFloorDivAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                FloorDivAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyModuloAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                ModuloAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyMatriceAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                MatriceAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyBitAndAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                BitwiseAndAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyBitOrAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                BitwiseOrAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyBitXorAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                BitwiseXorAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyShiftLeftAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                ShiftLeftAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyShiftRightAssign -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestList()
                ShiftRightAssignNode(start, tokenizer.curIndex, left, symbol, right)
            }
            TokenCode.PyColon -> {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val right = parseTest(true)
                var symbol2 = Token(TokenCode.Empty)
                var next = BaseNode(-1, -1)
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyAssign) {
                    symbol2 = tokenizer.curSymbol
                    tokenizer.advance()
                    next = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestListStarExpr()
                }
                AnnAssignNode(start, tokenizer.curIndex, left, symbol1, right, symbol2, next)
            }
            TokenCode.PyAssign -> {
                while (tokenizer.curSymbol.tokenKind == TokenCode.PyAssign) {
                    val symbol1 = tokenizer.curSymbol
                    tokenizer.advance()
                    val right = if (tokenizer.curSymbol.tokenKind == TokenCode.PyYield) parseYieldExpr() else parseTestListStarExpr()
                    left = AssignNode(start, tokenizer.curIndex, left, symbol1, right)
                }
                if (tokenizer.curSymbol.tokenKind == TokenCode.TypeComment && left is AssignNode) {
                    left.addTypeCommentNode(tokenizer.curSymbol)
                    tokenizer.advance()
                }
                left
            }
            else ->  left
        }
    }

    private fun parseTestListStarExpr() : BaseNode {
        val start = tokenizer.curIndex
        val firstNode = if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseStarExpr() else parseTest(true)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val nodes = mutableListOf<BaseNode>()
            val separators = mutableListOf<Token>()
            nodes.add(firstNode)
            val stopToken = setOf(
                TokenCode.PyPlusAssign,
                TokenCode.PyMinusAssign,
                TokenCode.PyMulAssign,
                TokenCode.PyPowerAssign,
                TokenCode.PyModuloAssign,
                TokenCode.PyMatriceAssign,
                TokenCode.PyBitAndAssign,
                TokenCode.PyBitXorAssign,
                TokenCode.PyBitOrAssign,
                TokenCode.PyShiftLeftAssign,
                TokenCode.PyShiftRightAssign,
                TokenCode.PyDivAssign,
                TokenCode.PyFloorDivAssign,
                TokenCode.PyColon,
                TokenCode.PyAssign,
                TokenCode.PySemiColon,
                TokenCode.Newline
            )
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind in stopToken) break;
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    throw SyntaxError(tokenizer.curIndex, "Unexpected ',' in list!")
                }
                nodes.add(if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseStarExpr() else parseTest(true))
            }
            return TestListStarExprNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return firstNode
    }

    private fun parseDelStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyDel)
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        val right = parseExprList()
        return DelStmtNode(start, tokenizer.curIndex, symbol, right)
    }

    private fun parsePassStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyPass)
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        return PassStmtNode(start, tokenizer.curIndex, symbol)
    }

    private fun parseFlowStmt() : BaseNode {
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyBreak -> parseBreakStmt()
            TokenCode.PyContinue -> parseContinueStmt()
            TokenCode.PyRaise -> parseRaiseStmt()
            TokenCode.PyYield -> parseYieldExpr()
            TokenCode.PyReturn -> parseReturnStmt()
            else -> {
                throw SyntaxError(tokenizer.curIndex, "Internal Parser error in FlowStmt Rule!")
            }
        }
    }

    private fun parseBreakStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyBreak)
        if (level <= 0) {
            throw SyntaxError(tokenizer.curIndex, "Break statement outside of loop statement!")
        }
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        return BreakStmtNode(start, tokenizer.curIndex, symbol)
    }

    private fun parseContinueStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyContinue)
        if (level <= 0) {
            throw SyntaxError(tokenizer.curIndex, "Continue statement outside of loop statement!")
        }
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        return ContinueStmtNode(start, tokenizer.curIndex, symbol)
    }

    private fun parseReturnStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyReturn)
        if (funcLevel <= 0) {
            throw SyntaxError(tokenizer.curIndex, "Return statement outside of function!")
        }
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        var right = BaseNode(-1, -1)
        if (tokenizer.curSymbol.tokenKind != TokenCode.Newline && tokenizer.curSymbol.tokenKind != TokenCode.PySemiColon) {
            right = parseTestListStarExpr()
        }
        return ReturnStmtNode(start, tokenizer.curIndex, symbol, right)
    }

    private fun parseYieldStmt() : BaseNode {
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyYield)
        if (level <= 0) {
            throw SyntaxError(tokenizer.curIndex, "Yield statement outside of loop statement!")
        }
        return parseYieldExpr()
    }

    private fun parseRaiseStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyRaise)
        val symbol = tokenizer.curSymbol
        tokenizer.advance()
        var left = BaseNode(-1, -1)
        var symbol2 = Token(TokenCode.Empty)
        var right = BaseNode(-1, -1)
        if (tokenizer.curSymbol.tokenKind != TokenCode.Newline && tokenizer.curSymbol.tokenKind != TokenCode.PySemiColon) {
            left = parseTest(true)
            if (tokenizer.curSymbol.tokenKind == TokenCode.PyFrom) {
                symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                right = parseTest(true)
            }
        }
        return RaiseStmtNode(start, tokenizer.curIndex, symbol, left, symbol2, right)
    }

    private fun parseImportStmt() : BaseNode {
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyImport -> parseImportName()
            TokenCode.PyFrom -> parseImportFrom()
            else -> {
                throw SyntaxError(tokenizer.curIndex, "Internal Parser error in ImportStmt Rule!")
            }
        }
    }

    private fun parseImportName() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyImport)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val right = parseDottedAsNames()
        return ImportNameNode(start, tokenizer.curIndex, symbol1, right)
    }

    private fun parseImportFrom() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyFrom)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val dots = mutableListOf<Token>()
        while (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyDot, TokenCode.PyElipsis)) {
            dots.add(tokenizer.curSymbol)
            tokenizer.advance()
        }
        if (dots.isEmpty() && tokenizer.curSymbol.tokenKind == TokenCode.PyImport) {
            throw SyntaxError(tokenizer.curIndex, "Expecting 'import' in 'from' statement!")
        }
        val left = if (tokenizer.curSymbol.tokenKind != TokenCode.PyImport) parseDottedName() else null
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyImport) {
            throw SyntaxError(tokenizer.curIndex, "Expecting 'import' in 'from' statement!")
        }
        val symbol2 = tokenizer.curSymbol
        tokenizer.advance()
        var symbol3: Token? = null
        var right: BaseNode? = null
        var symbol4: Token? = null

        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyMul -> {
                symbol3 = tokenizer.curSymbol
                tokenizer.advance()
            }
            TokenCode.PyLeftParen -> {
                symbol3 = tokenizer.curSymbol
                tokenizer.advance()
                right = parseImportAsNames()
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                    throw SyntaxError(tokenizer.curIndex, "Missing ')' in from import statement!")
                }
                symbol4 = tokenizer.curSymbol
                tokenizer.advance()
            }
            else -> {
                right = parseImportAsNames()
            }
        }
        return ImportFromNode(start, tokenizer.curIndex, symbol1, left, dots.toTypedArray(), symbol2, symbol3, right, symbol4)
    }

    private fun parseImportAsName() : BaseNode {
        val start = tokenizer.curIndex
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal in 'import' statement!")
        }
        val nameLiteral = tokenizer.curSymbol
        tokenizer.advance()
        val left = NameLiteralNode(start, tokenizer.curIndex, nameLiteral)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyAs) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal in 'import' as statement!")
            }
            val name2Literal = tokenizer.curSymbol
            tokenizer.advance()
            val right = NameLiteralNode(start, tokenizer.curIndex, name2Literal)
            return ImportAsName(
                start,
                tokenizer.curIndex,
                left,
                symbol,
                right
            )
        }
        return left
    }

    private fun parseDottedAsName() : BaseNode {
        val start = tokenizer.curIndex
        val left = parseDottedName()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyAs) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal in 'import' as statement!")
            }
            val literalName = tokenizer.curSymbol
            tokenizer.advance()
            val right = NameLiteralNode(start, tokenizer.curIndex, literalName)
            return DottedAsName(start, tokenizer.curIndex, left, symbol, right)
        }
        return left
    }

    private fun parseImportAsNames() : BaseNode {
        val start = tokenizer.curIndex
        val left = parseImportAsName()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val separators = mutableListOf<Token>()
            val nodes = mutableListOf<BaseNode>()
            nodes.add(left)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                nodes.add(parseImportAsName())
            }
            return ImportAsNamesNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return left
    }

    private fun parseDottedAsNames() : BaseNode {
        val start = tokenizer.curIndex
        val left = parseDottedAsName()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            val separators = mutableListOf<Token>()
            val nodes = mutableListOf<BaseNode>()
            nodes.add(left)
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separators.add(tokenizer.curSymbol)
                tokenizer.advance()
                nodes.add(parseDottedAsName())
            }
            return DottedAsNamesNode(start, tokenizer.curIndex, nodes.toTypedArray(), separators.toTypedArray())
        }
        return left
    }

    private fun parseDottedName() : BaseNode {
        val start = tokenizer.curIndex
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal in 'import' statement!")
        }
        val nameNodes = mutableListOf<LiteralBaseNode>()
        val separators = mutableListOf<Token>()
        var nameLiteral = tokenizer.curSymbol
        tokenizer.advance()
        nameNodes.add(NameLiteralNode(start, tokenizer.curIndex, nameLiteral))
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyDot) {
            separators.add(tokenizer.curSymbol)
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal in 'import' statement!")
            }
            nameLiteral = tokenizer.curSymbol
            tokenizer.advance()
            nameNodes.add(NameLiteralNode(start, tokenizer.curIndex, nameLiteral))
        }
        return DottedNameNode(start, tokenizer.curIndex, nameNodes.toTypedArray(), separators.toTypedArray())
    }

    private fun parseGlobalStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyGlobal)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting literal NAME in 'global' statement!")
        }
        val nodes = mutableListOf<NameLiteralNode>()
        val separator = mutableListOf<Token>()
        var literalName = tokenizer.curSymbol
        tokenizer.advance()
        nodes.add(NameLiteralNode(start, tokenizer.curIndex, literalName))
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            separator.add(tokenizer.curSymbol)
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                throw SyntaxError(tokenizer.curIndex, "Expecting literal NAME after ',' in 'global' statement!")
            }
            literalName = tokenizer.curSymbol
            tokenizer.advance()
            nodes.add(NameLiteralNode(start, tokenizer.curIndex, literalName))
        }
        return GlobalStmtNode(start, tokenizer.curIndex, symbol1, nodes.toTypedArray(), separator.toTypedArray())
    }

    private fun parseNonlocalStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyNonlocal)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
            throw SyntaxError(tokenizer.curIndex, "Expecting literal NAME in 'nonlocall' statement!")
        }

        val nodes = mutableListOf<NameLiteralNode>()
        val separator = mutableListOf<Token>()
        var literalName = tokenizer.curSymbol
        tokenizer.advance()
        nodes.add(NameLiteralNode(start, tokenizer.curIndex, literalName))
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            separator.add(tokenizer.curSymbol)
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                throw SyntaxError(tokenizer.curIndex, "Expecting literal NAME after ',' in 'nonlocal' statement!")
            }
            literalName = tokenizer.curSymbol
            tokenizer.advance()
            nodes.add(NameLiteralNode(start, tokenizer.curIndex, literalName))
        }
        return NonlocalStmtNode(start, tokenizer.curIndex, symbol1, nodes.toTypedArray(), separator.toTypedArray())
    }

    private fun parseAssertStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyAssert)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val left = parseTest(true)
        var symbol2 = Token(TokenCode.Empty)
        var right = BaseNode(-1, -1)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            symbol2 = tokenizer.curSymbol
            tokenizer.advance()
            right = parseTest(true)
        }
        return AssertStmtNode(start, tokenizer.curIndex, symbol1, left, symbol2, right)
    }

    private fun parseCompoundStmt() : BaseNode {
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyIf -> parseIfStmt()
            TokenCode.PyWhile -> parseWhileStmt()
            TokenCode.PyFor -> parseForStmt()
            TokenCode.PyTry -> parseTryStmt()
            TokenCode.PyWith -> parseWithStmt()
            TokenCode.PyDef,
            TokenCode.PyMatrice ->  throw NotImplementedError()
            TokenCode.PyAsync -> parseAsyncStmt()
            TokenCode.PyClass ->    throw NotImplementedError()
            else -> {
                throw SyntaxError(tokenizer.curIndex, "Internal parse error in compound statement selector rule!")
            }
        }
    }

    private fun parseAsyncStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyAsync)
        val symbol1 = tokenizer.curSymbol   // async
        tokenizer.advance()
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyDef ->  throw NotImplementedError()
            TokenCode.PyFor ->  {
                val right = parseForStmt()
                AsyncStmtNode(start, tokenizer.curIndex, symbol1, right)
            }
            TokenCode.PyWith -> {
                val right = parseWithStmt()
                AsyncStmtNode(start, tokenizer.curIndex, symbol1, right)
            }
            else -> {
                throw SyntaxError(tokenizer.curIndex, "")
            }
        }
    }

    private fun parseIfStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyIf)
        val symbol1 = tokenizer.curSymbol   // if
        tokenizer.advance()
        val left = parseNamedExpr()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'if' statement!")
        }
        val symbol2 = tokenizer.curSymbol   // :
        tokenizer.advance()
        val right = parseSuite()
        if (tokenizer.curSymbol.tokenKind !in setOf(TokenCode.PyElif, TokenCode.PyElse)) {
            return IfStmtNode(start, tokenizer.curIndex, symbol1, left, symbol2, right, null, null)
        }
        val elifNodes = mutableListOf<BaseNode>()
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyElif) {
            val start2 = tokenizer.curIndex
            val symbol3 = tokenizer.curSymbol   // elif
            tokenizer.advance()
            val left2 = parseNamedExpr()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
                throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'elif' statement!")
            }
            val symbol4 = tokenizer.curSymbol
            tokenizer.advance()
            val right2 = parseSuite()
            elifNodes.add(ElifStmtNode(start2, tokenizer.curIndex, symbol3, left2, symbol4, right2))
        }
        val elseNode = if (tokenizer.curSymbol.tokenKind == TokenCode.PyElse) parseElseStmt() else null
        return IfStmtNode(start, tokenizer.curIndex, symbol1, left, symbol2, right, if (elifNodes.isEmpty()) null else elifNodes.toTypedArray(), elseNode)
    }

    private fun parseElseStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyElse)
        val symbol1 = tokenizer.curSymbol   // else
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'else' statement!")
        }
        val symbol2 = tokenizer.curSymbol
        tokenizer.advance()
        val right = parseSuite()
        return ElseStmtNode(start, tokenizer.curIndex, symbol1, symbol2, right)
    }

    private fun parseWhileStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyWhile)
        val symbol1 = tokenizer.curSymbol   // while
        tokenizer.advance()
        val left = parseNamedExpr()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'while' statement!")
        }
        val symbol2 = tokenizer.curSymbol   // :
        tokenizer.advance()
        val right = parseSuite()
        val next = if (tokenizer.curSymbol.tokenKind == TokenCode.PyElse) parseElseStmt() else null
        return WhileStmtNode(start, tokenizer.curIndex, symbol1, left, symbol2, right, next)
    }

    private fun parseForStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyFor)
        val symbol1 = tokenizer.curSymbol   // for
        tokenizer.advance()
        val left = parseExprList()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyIn) {
            throw SyntaxError(tokenizer.curIndex, "Expecting 'in' in 'while' statement!")
        }
        val symbol2 = tokenizer.curSymbol   // in
        tokenizer.advance()
        val right = parseTestList()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'for' statement!")
        }
        val symbol3 = tokenizer.curSymbol   // :
        tokenizer.advance()
        val typeComment = if (tokenizer.curSymbol.tokenKind == TokenCode.TypeComment) {
            val hold = tokenizer.curSymbol
            tokenizer.advance()
            hold
        } else null
        val next = parseSuite()
        val elseNode = if (tokenizer.curSymbol.tokenKind == TokenCode.PyElse) parseElseStmt() else null
        return ForStmtNode(start, tokenizer.curIndex, symbol1, left, symbol2, right, symbol3, typeComment, next, elseNode)
    }

    private fun parseTryStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyTry)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'try' statement!")
        }
        val symbol2 = tokenizer.curSymbol   // :
        tokenizer.advance()
        val left = parseSuite()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyFinally) {
            val start2 = tokenizer.curIndex
            val symbol3 = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
                throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'finally' statement!")
            }
            val symbol4 = tokenizer.curSymbol   // :
            tokenizer.advance()
            val right = parseSuite()
            val fin = FinallyStmtNode(start2, tokenizer.curIndex, symbol3, symbol4, right)
            return TryStmtNode(start, tokenizer.curIndex, symbol1, symbol2, left, null, null, fin)
        }
        val nodes = mutableListOf<BaseNode>()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyExcept) {
            throw SyntaxError(tokenizer.curIndex, "Expecting 'except' or 'finally' in 'try' statement!")
        }
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyExcept) {
            val start3 = tokenizer.curIndex
            val left2 = parseExceptClause()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
                throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'except' statement!")
            }
            val symbol5 = tokenizer.curSymbol
            tokenizer.advance()
            val right2 = parseSuite()
            nodes.add(ExceptStmtNode(start3, tokenizer.curIndex, left2, symbol5, right2))
        }
        val elseNode = if (tokenizer.curSymbol.tokenKind == TokenCode.PyElse) parseElseStmt() else null
        val fin = if (tokenizer.curSymbol.tokenKind == TokenCode.PyFinally) {
            val start3 = tokenizer.curIndex
            val symbol8 = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
                throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'finally' statement!")
            }
            val symbol9 = tokenizer.curSymbol   // :
            tokenizer.advance()
            val right3 = parseSuite()
            FinallyStmtNode(start3, tokenizer.curIndex, symbol8, symbol9, right3)
        } else null
        return TryStmtNode(start, tokenizer.curIndex, symbol1, symbol2, left, nodes.toTypedArray(), elseNode, fin)
    }

    private fun parseWithStmt() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyWith)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val items = mutableListOf<BaseNode>()
        val separators = mutableListOf<Token>()
        items.add(parseWithItem())
        while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
            separators.add(tokenizer.curSymbol)
            tokenizer.advance()
            items.add(parseWithItem())
        }
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in 'with' statement!")
        }
        val symbol2 = tokenizer.curSymbol   // :
        tokenizer.advance()
        val typeComment = if (tokenizer.curSymbol.tokenKind == TokenCode.TypeComment) {
            val hold = tokenizer.curSymbol
            tokenizer.advance()
            hold
        } else null
        val right = parseSuite()
        return WithStmtNode(start, tokenizer.curIndex, symbol1, items.toTypedArray(), separators.toTypedArray(), symbol2, typeComment, right )
    }

    private fun parseWithItem() : BaseNode {
        val start = tokenizer.curIndex
        val left = parseTest(true)
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyAs) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            val right = parseBitwiseOr()
            return WithItemNode(start, tokenizer.curIndex, left, symbol, right)
        }
        return left
    }

    private fun parseExceptClause() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyExcept)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        var left : BaseNode? = null
        var symbol2 : Token? = null
        var right : BaseNode? = null
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
            left = parseTest(true)
            if (tokenizer.curSymbol.tokenKind == TokenCode.PyAs) {
                symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind != TokenCode.NAME) {
                    throw SyntaxError(tokenizer.curIndex, "Expecting NAME literal after 'as' in except clause!")
                }
                val nameLiteral = tokenizer.curSymbol
                tokenizer.advance()
                right = NameLiteralNode(start, tokenizer.curIndex, nameLiteral)
            }
        }
        return ExceptClauseNode(start, tokenizer.curIndex, symbol1, left, symbol2, right)
    }

    private fun parseSuite() : BaseNode {
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.Newline ->    {
                val start = tokenizer.curIndex
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                if (tokenizer.curSymbol.tokenKind != TokenCode.Indent) {
                    throw SyntaxError(tokenizer.curIndex, "Expecting indent in code block!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                val nodes = mutableListOf<BaseNode>()
                nodes.add(parseStmt())
                while (tokenizer.curSymbol.tokenKind != TokenCode.Dedent) {
                    nodes.add(parseStmt())
                }
                val symbol3 = tokenizer.curSymbol
                tokenizer.advance()
                SuiteNode(start, tokenizer.curIndex, symbol1, symbol2, nodes.toTypedArray(), symbol3)
            }
            else -> parseSimpleStmt()
        }
    }

    // Expression rules below!

    private fun parseAtom() : BaseNode {
        val start = tokenizer.curIndex
        return when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyFalse -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                FalseLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyNone -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                NoneLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyTrue -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                TrueLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyElipsis -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                ElipsisLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.NAME -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                NameLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.NUMBER -> {
                val symbol = tokenizer.curSymbol
                tokenizer.advance()
                NumberLiteralNode(start, tokenizer.curIndex, symbol)
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
                    StringArrayLiteralNode(start, tokenizer.curIndex, nodes.toTypedArray())
                }
                StringLiteralNode(start, tokenizer.curIndex, symbol)
            }
            TokenCode.PyLeftParen -> {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val right = when (tokenizer.curSymbol.tokenKind) {
                    TokenCode.PyYield -> parseYieldExpr()
                    TokenCode.PyRightParen ->   null
                    else -> {
                        parseTestListComp()
                    }
                }
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
                    throw SyntaxError(tokenizer.curIndex, "Expecting ')' in tuple!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                TupleNode(start, tokenizer.curIndex, symbol1, right, symbol2)
            }
            TokenCode.PyLeftBracket -> {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val right = when (tokenizer.curSymbol.tokenKind) {
                    TokenCode.PyRightBracket ->   null
                    else -> {
                        parseTestListComp()
                    }
                }
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightBracket) {
                    throw SyntaxError(tokenizer.curIndex, "Expecting ']' in list!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                ListNode(start, tokenizer.curIndex, symbol1, right, symbol2)
            }
            TokenCode.PyLeftCurly -> {
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val right = when (tokenizer.curSymbol.tokenKind) {
                    TokenCode.PyRightCurly ->   null
                    else -> {
                        parseDictorSetMaker()
                    }
                }
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightBracket) {
                    if (right is DictionaryContainerNode)
                        throw SyntaxError(tokenizer.curIndex, "Expecting '}' in dictionary!")
                    else if (right is SetContainerNode)
                        throw SyntaxError(tokenizer.curIndex, "Expecting '}' in set!")
                    else
                        throw SyntaxError(tokenizer.curIndex, "Internal parse error in dictionary/set rule!")
                }
                val symbol2 = tokenizer.curSymbol
                tokenizer.advance()
                if (right is DictionaryContainerNode)
                    DictionaryNode(start, tokenizer.curIndex, symbol1, right, symbol2)
                else if (right is SetContainerNode)
                    SetNode(start, tokenizer.curIndex, symbol1, right, symbol2)
                else
                    DictionaryNode(start, tokenizer.curIndex, symbol1, right, symbol2)
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
            TokenCode.PyPlus,
            TokenCode.PyMinus,
            TokenCode.PyBitInvert -> {
                tokenizer.advance()
                return FactorUnaryPlusNode(start, tokenizer.curIndex, symbol, parseFactor())
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
            left = parseVarArgsList()
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
            TokenCode.PyMul,
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
        val start = tokenizer.curIndex
        var isDictionary = true
        var key: BaseNode? = null
        var symbol : Token? = null
        var value: BaseNode? = null
        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyMul -> {
                isDictionary = false
                key = parseStarExpr()
            }
            TokenCode.PyPower -> {
                val start2 = tokenizer.curIndex
                val symbol1 = tokenizer.curSymbol
                tokenizer.advance()
                val right = parseTest(true)
                key = PowerKeyNode(start2, tokenizer.curIndex, symbol1, right)
            }
            else -> {
                key = parseTest(true)
                if (tokenizer.curSymbol.tokenKind == TokenCode.PyColon) {
                    symbol = tokenizer.curSymbol
                    tokenizer.advance()
                    value = parseTest(true)
                }
                else {
                    isDictionary = false
                }
            }
        }

        if (isDictionary) {
            val nodes = mutableListOf<BaseNode>()
            val separator = mutableListOf<Token>()
            nodes.add(if (key is PowerKeyNode) key else {
                if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
                    throw SyntaxError(tokenizer.curIndex, "Expecting ':' in dictionary entry!")
                }
                symbol = tokenizer.curSymbol
                tokenizer.advance()
                value = parseTest(true)
                KeyValueNode(start, tokenizer.curIndex, key, symbol, value)
            })
            if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyFor, TokenCode.PyAsync)) {
                nodes.add(parseCompFor())
                return DictionaryContainerNode(start, tokenizer.curIndex, nodes.toTypedArray(), separator.toTypedArray())
            }
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separator.add(tokenizer.curSymbol)
                tokenizer.advance()
                key = null; symbol = null; value = null;
                when (tokenizer.curSymbol.tokenKind) {
                    TokenCode.PyComma -> {
                        throw SyntaxError(tokenizer.curIndex, "Unexpected ',' in dictionary!")
                    }
                    TokenCode.PyRightCurly -> break
                    TokenCode.PyPower -> {
                        val start2 = tokenizer.curIndex
                        val symbol1 = tokenizer.curSymbol
                        tokenizer.advance()
                        val right = parseTest(true)
                        nodes.add(PowerKeyNode(start2, tokenizer.curIndex, symbol1, right))
                    }
                    else -> {
                        key = parseTest(true)
                        if (tokenizer.curSymbol.tokenKind != TokenCode.PyColon) {
                            throw SyntaxError(tokenizer.curIndex, "Expecting ':' in dictionary entry!")
                        }
                        symbol = tokenizer.curSymbol
                        tokenizer.advance()
                        value = parseTest(true)
                        nodes.add(KeyValueNode(start, tokenizer.curIndex, key, symbol, value))
                    }
                }
            }
            return DictionaryContainerNode(start, tokenizer.curIndex, nodes.toTypedArray(), separator.toTypedArray())
        } else {
            val nodes = mutableListOf<BaseNode>()
            val separator = mutableListOf<Token>()
            nodes.add(key)
            if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyFor, TokenCode.PyAsync)) {
                nodes.add(parseCompFor())
                return SetContainerNode(start, tokenizer.curIndex, nodes.toTypedArray(), separator.toTypedArray())
            }
            while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                separator.add(tokenizer.curSymbol)
                tokenizer.advance()
                when (tokenizer.curSymbol.tokenKind) {
                    TokenCode.PyComma -> {
                        throw SyntaxError(tokenizer.curIndex, "Unexpected ',' in set!")
                    }
                    TokenCode.PyRightCurly -> break
                    else -> {
                        nodes.add(if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) parseBitwiseOr() else parseTest(true))
                    }
                }
            }
            return SetContainerNode(start, tokenizer.curIndex, nodes.toTypedArray(), separator.toTypedArray())
        }
    }

    private fun parseCompIter() : BaseNode {
        assert(tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyFor, TokenCode.PyAsync, TokenCode.PyIf))
        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyAsync,
            TokenCode.PyFor,
            TokenCode.PyIf -> {
                return parseCompFor()
            }
            else -> {
                throw NotImplementedError() // Should never happen due to assert statement,
            }
        }
    }

    private fun parseSyncCompFor() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyFor)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val left = parseExprList()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyIn) {
            throw SyntaxError(tokenizer.curIndex, "Missing 'in' in for comprehension expression!")
        }
        val symbol2 = tokenizer.curSymbol
        tokenizer.advance()
        val right = parseOrTest()
        var next = BaseNode(-1, -1)
        if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyAsync, TokenCode.PyFor, TokenCode.PyIf)) {
            next = parseCompIter()
        }
        return CompSyncForNode(start, tokenizer.curIndex, symbol1, left, symbol2, right, next)
    }

    private fun parseCompFor() : BaseNode {
        val start = tokenizer.curIndex
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyAsync) {
            val symbol = tokenizer.curSymbol
            tokenizer.advance()
            if (tokenizer.curSymbol.tokenKind != TokenCode.PyFor) {
                throw SyntaxError(tokenizer.curIndex, "Expecting 'for' after async comprehension expression!")
            }
            val right = parseSyncCompFor()
            return CompForNode(start, tokenizer.curIndex, symbol, right)
        }
        return parseSyncCompFor()
    }

    private fun parseCompIf() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyIf)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val left = parseTest(false)
        var next = BaseNode(-1, -1)
        if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.PyAsync, TokenCode.PyFor, TokenCode.PyIf)) {
            next = parseCompIter()
        }
        return CompIfNode(start, tokenizer.curIndex, symbol1, left, next)
    }

    private fun parseYieldExpr() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyYield)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind == TokenCode.PyFrom) {
            val symbol2 = tokenizer.curSymbol
            tokenizer.advance()
            val right = parseTest(true)
            return YieldFromNode(start, tokenizer.curIndex, symbol1, symbol2, right)
        }
        val right = parseTestListStarExpr()
        return YieldNode(start, tokenizer.curIndex, symbol1, right)
    }

    // Func rules below!

    private fun parseFuncBodySuite() : BaseNode {
        throw NotImplementedError()
    }

    private fun parseFuncType() : BaseNode {
        val start = tokenizer.curIndex
        assert(tokenizer.curSymbol.tokenKind == TokenCode.PyLeftParen)
        val symbol1 = tokenizer.curSymbol
        tokenizer.advance()
        val left = if (tokenizer.curSymbol.tokenKind == TokenCode.PyRightParen) null else parseTypedList()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyRightParen) {
            throw SyntaxError(tokenizer.curIndex, "Expecting ')' in function type!")
        }
        val symbol2 = tokenizer.curSymbol
        tokenizer.advance()
        if (tokenizer.curSymbol.tokenKind != TokenCode.PyArrow) {
            throw SyntaxError(tokenizer.curIndex, "Expecting '->' in function type!")
        }
        val symbol3 = tokenizer.curSymbol
        tokenizer.advance()
        val right = parseTest(true)
        return FuncTypeNode(start, tokenizer.curIndex, symbol1, left, symbol2, symbol3, right)
    }

    private fun parseTypedList() : BaseNode {
        val start = tokenizer.curIndex
        var mulOp: Token? = null
        var left: BaseNode? = null
        var powerOp: Token? = null
        var right: BaseNode? = null
        val nodes = mutableListOf<BaseNode>()
        val separators = mutableListOf<Token>()

        when (tokenizer.curSymbol.tokenKind) {
            TokenCode.PyPower -> {
                powerOp = tokenizer.curSymbol
                tokenizer.advance()
                right = parseTest(true)
            }
            TokenCode.PyMul -> {
                mulOp = tokenizer.curSymbol
                tokenizer.advance()
                left = parseTest(true)
                while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    separators.add(tokenizer.curSymbol)
                    tokenizer.advance()
                    if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                        throw SyntaxError(tokenizer.curIndex, "Unexpected ',' in typed list!")
                    }
                    if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.Newline, TokenCode.EOF)) break
                    if (tokenizer.curSymbol.tokenKind == TokenCode.PyPower) {
                        powerOp = tokenizer.curSymbol
                        tokenizer.advance()
                        right = parseTest(true)
                        break
                    }
                    else nodes.add(parseTest(true))
                }
            }
            else -> {
                nodes.add(parseTest(true))
                outer@ while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                    separators.add(tokenizer.curSymbol)
                    tokenizer.advance()
                    if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                        throw SyntaxError(tokenizer.curIndex, "Unexpected ',' in typed list!")
                    }
                    if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.Newline, TokenCode.EOF)) break@outer
                    if (tokenizer.curSymbol.tokenKind == TokenCode.PyPower) {
                        powerOp = tokenizer.curSymbol
                        tokenizer.advance()
                        right = parseTest(true)
                        break@outer
                    } else if (tokenizer.curSymbol.tokenKind == TokenCode.PyMul) {
                        mulOp = tokenizer.curSymbol
                        tokenizer.advance()
                        left = parseTest(true)
                        while (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                            separators.add(tokenizer.curSymbol)
                            tokenizer.advance()
                            if (tokenizer.curSymbol.tokenKind == TokenCode.PyComma) {
                                throw SyntaxError(tokenizer.curIndex, "Unexpected ',' in typed list!")
                            }
                            if (tokenizer.curSymbol.tokenKind in setOf(TokenCode.Newline, TokenCode.EOF)) break@outer
                            if (tokenizer.curSymbol.tokenKind == TokenCode.PyPower) {
                                powerOp = tokenizer.curSymbol
                                tokenizer.advance()
                                right = parseTest(true)
                                break@outer
                            }
                            else nodes.add(parseTest(true))
                        }
                    } else nodes.add(parseTest(true))
                }
            }
        }

        return TypeListNode(
            start,
            tokenizer.curIndex,
            mulOp,
            left,
            powerOp,
            right,
            if (nodes.isEmpty()) null else nodes.toTypedArray(),
            if (separators.isEmpty()) null else separators.toTypedArray()
        )
    }

    // Match rules below! 3.10 extension

}