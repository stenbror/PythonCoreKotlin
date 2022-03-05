package net.pythoncore.tests

import net.pythoncore.parser.*
import net.pythoncore.parser.ast.*
import org.junit.Test
import kotlin.test.assertEquals

// Mocked tokenizer for all UnitTests in Parser module
class MockedPythonCoreTokenizer(val tokens: Array<Pair<Token, Int>>) : IPythonCoreTokenizer {
    private var index = -1

    override var curSymbol: Token
        get() = if (index < tokens.count()) tokens.get(index).first else Token(TokenCode.EOF)
        set(value) {}

    override var curIndex: Int
        get() = if (index < tokens.count()) tokens.get(index).second else -1
        set(value) {}

    override fun advance() {
        index++
    }
}

class ParserTest {

    @Test
    fun testAtomNone() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyNone), 0),
            Pair(Token(TokenCode.EOF), 4)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is NoneLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as NoneLiteralNode).nodeStartPos )
        assertEquals(4, ((node as EvalInputNode).rightNode as NoneLiteralNode).nodeEndPos )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomFalse() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyFalse), 0),
            Pair(Token(TokenCode.EOF), 5)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is FalseLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as FalseLiteralNode).nodeStartPos )
        assertEquals(5, ((node as EvalInputNode).rightNode as FalseLiteralNode).nodeEndPos )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomTrue() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyTrue), 0),
            Pair(Token(TokenCode.EOF), 4)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TrueLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TrueLiteralNode).nodeStartPos )
        assertEquals(4, ((node as EvalInputNode).rightNode as TrueLiteralNode).nodeEndPos )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomElipsis() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyElipsis), 0),
            Pair(Token(TokenCode.EOF), 3)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ElipsisLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ElipsisLiteralNode).nodeStartPos )
        assertEquals(3, ((node as EvalInputNode).rightNode as ElipsisLiteralNode).nodeEndPos )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomNameLiteral() {
        val tokens = arrayOf(
            Pair(NameToken(0, 8, "__init__"), 0),
            Pair(Token(TokenCode.EOF), 8)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is NameLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as NameLiteralNode).nodeStartPos )
        assertEquals(8, ((node as EvalInputNode).rightNode as NameLiteralNode).nodeEndPos )
        assertEquals("__init__", (((node as EvalInputNode).rightNode as NameLiteralNode).symbolOne as NameToken).textData )
        assertEquals(0, (((node as EvalInputNode).rightNode as NameLiteralNode).symbolOne as NameToken).startPosition )
        assertEquals(8, (((node as EvalInputNode).rightNode as NameLiteralNode).symbolOne as NameToken).endPosition )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomNumberLiteral() {
        val tokens = arrayOf(
            Pair(NumberToken(0, 5, "0.34J"), 0),
            Pair(Token(TokenCode.EOF), 5)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is NumberLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as NumberLiteralNode).nodeStartPos )
        assertEquals(5, ((node as EvalInputNode).rightNode as NumberLiteralNode).nodeEndPos )
        assertEquals("0.34J", (((node as EvalInputNode).rightNode as NumberLiteralNode).symbolOne as NumberToken).textData )
        assertEquals(0, (((node as EvalInputNode).rightNode as NumberLiteralNode).symbolOne as NumberToken).startPosition )
        assertEquals(5, (((node as EvalInputNode).rightNode as NumberLiteralNode).symbolOne as NumberToken).endPosition )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomStringMultipleLiteral() {
        val tokens = arrayOf(
            Pair(StringToken(0, 15, "'Hello, World!'"), 0),
            Pair(StringToken(15, 29, "'Another one!'"), 0),
            Pair(Token(TokenCode.EOF), 29)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is StringArrayLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as StringArrayLiteralNode).nodeStartPos )
        assertEquals(29, ((node as EvalInputNode).rightNode as StringArrayLiteralNode).nodeEndPos )
        assertEquals(0, (((node as EvalInputNode).rightNode as StringArrayLiteralNode).symbolNodes[0] as StringToken).startPosition )
        assertEquals(15, (((node as EvalInputNode).rightNode as StringArrayLiteralNode).symbolNodes[0] as StringToken).endPosition )
        assertEquals("'Hello, World!'", (((node as EvalInputNode).rightNode as StringArrayLiteralNode).symbolNodes[0] as StringToken).textData )
        assertEquals(15, (((node as EvalInputNode).rightNode as StringArrayLiteralNode).symbolNodes[1] as StringToken).startPosition )
        assertEquals(29, (((node as EvalInputNode).rightNode as StringArrayLiteralNode).symbolNodes[1] as StringToken).endPosition )
        assertEquals("'Another one!'", (((node as EvalInputNode).rightNode as StringArrayLiteralNode).symbolNodes[1] as StringToken).textData )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomStringLiteral() {
        val tokens = arrayOf(
            Pair(StringToken(0, 15, "'Hello, World!'"), 0),
            Pair(Token(TokenCode.EOF), 15)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is StringLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as StringLiteralNode).nodeStartPos )
        assertEquals(15, ((node as EvalInputNode).rightNode as StringLiteralNode).nodeEndPos )
        assertEquals(0, (((node as EvalInputNode).rightNode as StringLiteralNode).symbolOne as StringToken).startPosition )
        assertEquals(15, (((node as EvalInputNode).rightNode as StringLiteralNode).symbolOne as StringToken).endPosition )
        assertEquals("'Hello, World!'", (((node as EvalInputNode).rightNode as StringLiteralNode).symbolOne as StringToken).textData )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomTupleLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftParen), 0),
            Pair(Token(TokenCode.PyRightParen), 1),
            Pair(Token(TokenCode.EOF), 2)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TupleNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TupleNode).nodeStartPos )
        assertEquals(2, ((node as EvalInputNode).rightNode as TupleNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftParen, ((node as EvalInputNode).rightNode as TupleNode).symbolOne.tokenKind )
        assertEquals(null, ((node as EvalInputNode).rightNode as TupleNode).rightNode )
        assertEquals(TokenCode.PyRightParen, ((node as EvalInputNode).rightNode as TupleNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(Token(TokenCode.PyRightBracket), 1),
            Pair(Token(TokenCode.EOF), 2)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(2, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(null, ((node as EvalInputNode).rightNode as ListNode).rightNode )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(Token(TokenCode.PyRightCurly), 1),
            Pair(Token(TokenCode.EOF), 2)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(2, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(null, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode )
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListWithTestListWithSingleArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(NameToken(1, 2, "a"), 1),
            Pair(Token(TokenCode.PyRightBracket), 2),
            Pair(Token(TokenCode.EOF), 3)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(3, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as ListNode).rightNode is NameLiteralNode )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListWithTestListWithSingleStarArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyRightBracket), 3),
            Pair(Token(TokenCode.EOF), 4)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(4, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as ListNode).rightNode is BitwiseStarExpressionNode )
        assertEquals(TokenCode.PyMul, (((node as EvalInputNode).rightNode as ListNode).rightNode as BitwiseStarExpressionNode).symbolOne.tokenKind )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as BitwiseStarExpressionNode).rightNode is NameLiteralNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as ListNode).rightNode as BitwiseStarExpressionNode).nodeStartPos )
        assertEquals(3, (((node as EvalInputNode).rightNode as ListNode).rightNode as BitwiseStarExpressionNode).nodeEndPos )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListWithTestListWithMultipleStarArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyComma), 3),
            Pair(NameToken(4, 5, "b"), 4),
            Pair(Token(TokenCode.PyRightBracket), 5),
            Pair(Token(TokenCode.EOF), 6)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(6, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as ListNode).rightNode is TestListNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes.size )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[0] is BitwiseStarExpressionNode )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] is NameLiteralNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementerSeparators!!.size )
        assertEquals(TokenCode.PyComma, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementerSeparators!![0].tokenKind )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListWithTestTupleWithMultipleStarArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftParen), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyComma), 3),
            Pair(NameToken(4, 5, "b"), 4),
            Pair(Token(TokenCode.PyRightParen), 5),
            Pair(Token(TokenCode.EOF), 6)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TupleNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TupleNode).nodeStartPos )
        assertEquals(6, ((node as EvalInputNode).rightNode as TupleNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftParen, ((node as EvalInputNode).rightNode as TupleNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as TupleNode).rightNode is TestListNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as TupleNode).rightNode as TestListNode).elementNodes.size )
        assertEquals(true, (((node as EvalInputNode).rightNode as TupleNode).rightNode as TestListNode).elementNodes[0] is BitwiseStarExpressionNode )
        assertEquals(true, (((node as EvalInputNode).rightNode as TupleNode).rightNode as TestListNode).elementNodes[1] is NameLiteralNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as TupleNode).rightNode as TestListNode).elementerSeparators!!.size )
        assertEquals(TokenCode.PyComma, (((node as EvalInputNode).rightNode as TupleNode).rightNode as TestListNode).elementerSeparators!![0].tokenKind )
        assertEquals(TokenCode.PyRightParen, ((node as EvalInputNode).rightNode as TupleNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListWithTestListWithMultipleStarAndCompForArgumentLiteral() {
        val tokens = arrayOf( // [*a async for b in c]
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyAsync), 4),
            Pair(Token(TokenCode.PyFor), 10), // 14
            Pair(NameToken(14, 15, "b"), 14),
            Pair(Token(TokenCode.PyIn), 16),
            Pair(NameToken(14, 15, "c"), 14),
            Pair(Token(TokenCode.PyRightBracket), 15),
            Pair(Token(TokenCode.EOF), 16)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(16, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as ListNode).rightNode is TestListNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes.size )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[0] is BitwiseStarExpressionNode )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] is CompForNode )
        assertEquals(4, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).nodeStartPos )
        assertEquals(15, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).nodeEndPos )
        assertEquals(TokenCode.PyAsync, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).symbolOne.tokenKind )
        assertEquals(true, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode is CompSyncForNode )
        assertEquals(TokenCode.PyFor, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).symbolOne.tokenKind )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).leftNode is NameLiteralNode )
        assertEquals(TokenCode.PyIn, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).symbolTwo.tokenKind )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).rightNode is NameLiteralNode )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode is BaseNode )
        assertEquals(null, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementerSeparators )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }
}
