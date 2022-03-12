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

    @Test
    fun testAtomListWithTestListWithMultipleStarAndCompForAndIfArgumentLiteral() {
        val tokens = arrayOf( // [*a async for b in c]
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyAsync), 4),
            Pair(Token(TokenCode.PyFor), 10), // 14
            Pair(NameToken(14, 15, "b"), 14),
            Pair(Token(TokenCode.PyIn), 16),
            Pair(NameToken(14, 15, "c"), 14),
            Pair(Token(TokenCode.PyIf), 16),
            Pair(NameToken(19, 20, "d"), 19),
            Pair(Token(TokenCode.PyRightBracket), 20),
            Pair(Token(TokenCode.EOF), 21)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(21, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as ListNode).rightNode is TestListNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes.size )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[0] is BitwiseStarExpressionNode )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] is CompForNode )
        assertEquals(4, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).nodeStartPos )
        assertEquals(20, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).nodeEndPos )
        assertEquals(TokenCode.PyAsync, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).symbolOne.tokenKind )
        assertEquals(true, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode is CompSyncForNode )
        assertEquals(TokenCode.PyFor, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).symbolOne.tokenKind )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).leftNode is NameLiteralNode )
        assertEquals(TokenCode.PyIn, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).symbolTwo.tokenKind )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).rightNode is NameLiteralNode )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode is CompIfNode )
        assertEquals(16, ((((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode as CompIfNode).nodeStartPos )
        assertEquals(20, ((((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode as CompIfNode).nodeEndPos )
        assertEquals(TokenCode.PyIf, ((((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode as CompIfNode).symbolOne.tokenKind )
        assertEquals(true, ((((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode as CompIfNode).leftNode is NameLiteralNode )
        assertEquals(true, ((((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompForNode).rightNode as CompSyncForNode).nextNode as CompIfNode).nextNode is BaseNode )
        assertEquals(null, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementerSeparators )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomListWithTestListWithMultipleStarAndCompSyncForAndIfArgumentLiteral() {
        val tokens = arrayOf( // [*a for b in c]
            Pair(Token(TokenCode.PyLeftBracket), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyFor), 10), // 14
            Pair(NameToken(14, 15, "b"), 14),
            Pair(Token(TokenCode.PyIn), 16),
            Pair(NameToken(14, 15, "c"), 14),
            Pair(Token(TokenCode.PyIf), 16),
            Pair(NameToken(19, 20, "d"), 19),
            Pair(Token(TokenCode.PyRightBracket), 20),
            Pair(Token(TokenCode.EOF), 21)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is ListNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as ListNode).nodeStartPos )
        assertEquals(21, ((node as EvalInputNode).rightNode as ListNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftBracket, ((node as EvalInputNode).rightNode as ListNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as ListNode).rightNode is TestListNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes.size )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[0] is BitwiseStarExpressionNode )
        assertEquals(true, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] is CompSyncForNode)
        assertEquals(TokenCode.PyFor, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).symbolOne.tokenKind )
        assertEquals(true, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).leftNode is NameLiteralNode )
        assertEquals(TokenCode.PyIn, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).symbolTwo.tokenKind )
        assertEquals(true, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).rightNode is NameLiteralNode )
        assertEquals(true, ((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).nextNode is CompIfNode )
        assertEquals(16, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).nextNode as CompIfNode).nodeStartPos )
        assertEquals(20, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).nextNode as CompIfNode).nodeEndPos )
        assertEquals(TokenCode.PyIf, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).nextNode as CompIfNode).symbolOne.tokenKind )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).nextNode as CompIfNode).leftNode is NameLiteralNode )
        assertEquals(true, (((((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementNodes[1] as CompSyncForNode).nextNode as CompIfNode).nextNode is BaseNode )
        assertEquals(null, (((node as EvalInputNode).rightNode as ListNode).rightNode as TestListNode).elementerSeparators )
        assertEquals(TokenCode.PyRightBracket, ((node as EvalInputNode).rightNode as ListNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomTupleWithYieldArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftParen), 0),
            Pair(Token(TokenCode.PyYield), 2),
            Pair(NameToken(7, 8, "a"), 7),
            Pair(Token(TokenCode.PyRightParen), 9),
            Pair(Token(TokenCode.EOF), 10)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TupleNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TupleNode).nodeStartPos )
        assertEquals(10, ((node as EvalInputNode).rightNode as TupleNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftParen, ((node as EvalInputNode).rightNode as TupleNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as TupleNode).rightNode is YieldNode )
        assertEquals(TokenCode.PyYield, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).symbolOne.tokenKind )
        assertEquals(2, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).nodeStartPos )
        assertEquals(9, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).nodeEndPos )
        assertEquals(true, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode is NameLiteralNode )
        assertEquals(true, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as NameLiteralNode ).symbolOne is NameToken)
        assertEquals("a", (((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as NameLiteralNode ).symbolOne as NameToken).textData )
        assertEquals(TokenCode.PyRightParen, ((node as EvalInputNode).rightNode as TupleNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomTupleWithYieldTestListArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftParen), 0),
            Pair(Token(TokenCode.PyYield), 2),
            Pair(NameToken(7, 8, "a"), 7),
            Pair(Token(TokenCode.PyComma), 8),
            Pair(NameToken(9, 10, "b"), 9),
            Pair(Token(TokenCode.PyRightParen), 11),
            Pair(Token(TokenCode.EOF), 12)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TupleNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TupleNode).nodeStartPos )
        assertEquals(12, ((node as EvalInputNode).rightNode as TupleNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftParen, ((node as EvalInputNode).rightNode as TupleNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as TupleNode).rightNode is YieldNode )
        assertEquals(TokenCode.PyYield, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).symbolOne.tokenKind )
        assertEquals(2, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).nodeStartPos )
        assertEquals(11, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).nodeEndPos )
        assertEquals(true, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode is TestListStarExprNode )
        assertEquals(2, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementNodes.size )
        assertEquals(1, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementerSeparators!!.size )
        assertEquals(true, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementNodes[0] is NameLiteralNode )
        assertEquals(true, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementNodes[1] is NameLiteralNode )
        assertEquals(TokenCode.PyComma, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementerSeparators!![0].tokenKind  )
        assertEquals(TokenCode.PyRightParen, ((node as EvalInputNode).rightNode as TupleNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomTupleWithYieldTestListWithTrailingCommaArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftParen), 0),
            Pair(Token(TokenCode.PyYield), 2),
            Pair(NameToken(7, 8, "a"), 7),
            Pair(Token(TokenCode.PyComma), 8),
            Pair(NameToken(9, 10, "b"), 9),
            Pair(Token(TokenCode.PyComma), 10),
            Pair(Token(TokenCode.PyRightParen), 11),
            Pair(Token(TokenCode.EOF), 12)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TupleNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TupleNode).nodeStartPos )
        assertEquals(12, ((node as EvalInputNode).rightNode as TupleNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftParen, ((node as EvalInputNode).rightNode as TupleNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as TupleNode).rightNode is YieldNode )
        assertEquals(TokenCode.PyYield, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).symbolOne.tokenKind )
        assertEquals(2, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).nodeStartPos )
        assertEquals(11, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).nodeEndPos )
        assertEquals(true, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode is TestListStarExprNode )
        assertEquals(2, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementNodes.size )
        assertEquals(2, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementerSeparators!!.size )
        assertEquals(true, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementNodes[0] is NameLiteralNode )
        assertEquals(true, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementNodes[1] is NameLiteralNode )
        assertEquals(TokenCode.PyComma, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementerSeparators!![0].tokenKind  )
        assertEquals(TokenCode.PyComma, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldNode).rightNode as TestListStarExprNode ).elementerSeparators!![1].tokenKind  )
        assertEquals(TokenCode.PyRightParen, ((node as EvalInputNode).rightNode as TupleNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomTupleWithYieldFromArgumentLiteral() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftParen), 0),
            Pair(Token(TokenCode.PyYield), 2),
            Pair(Token(TokenCode.PyFrom), 7),
            Pair(NameToken(12, 13, "a"), 12),
            Pair(Token(TokenCode.PyRightParen), 14),
            Pair(Token(TokenCode.EOF), 15)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is TupleNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as TupleNode).nodeStartPos )
        assertEquals(15, ((node as EvalInputNode).rightNode as TupleNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftParen, ((node as EvalInputNode).rightNode as TupleNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as TupleNode).rightNode is YieldFromNode )
        assertEquals(TokenCode.PyYield, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).symbolOne.tokenKind )
        assertEquals(TokenCode.PyFrom, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).symbolTwo.tokenKind )
        assertEquals(2, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).nodeStartPos )
        assertEquals(14, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).nodeEndPos )
        assertEquals(true, (((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).rightNode is NameLiteralNode )
        assertEquals(true, ((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).rightNode as NameLiteralNode ).symbolOne is NameToken)
        assertEquals("a", (((((node as EvalInputNode).rightNode as TupleNode).rightNode as YieldFromNode).rightNode as NameLiteralNode ).symbolOne as NameToken).textData )
        assertEquals(TokenCode.PyRightParen, ((node as EvalInputNode).rightNode as TupleNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralSingle() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyRightCurly), 4),
            Pair(Token(TokenCode.EOF), 5)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(5, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(4, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralSingleStarExpr() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyRightCurly), 4),
            Pair(Token(TokenCode.EOF), 5)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(5, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(4, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is BitwiseStarExpressionNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralMultipleStarExpr() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyComma), 3),
            Pair(Token(TokenCode.PyMul), 5),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyRightCurly), 8),
            Pair(Token(TokenCode.EOF), 9)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(9, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(8, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is BitwiseStarExpressionNode)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] is BitwiseStarExpressionNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralMultipleStarExprWithTrailingComma() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyComma), 3),
            Pair(Token(TokenCode.PyMul), 5),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyComma), 7),
            Pair(Token(TokenCode.PyRightCurly), 8),
            Pair(Token(TokenCode.EOF), 9)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(9, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(8, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is BitwiseStarExpressionNode)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] is BitwiseStarExpressionNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralMultipleStarExprWithExtraArgument() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(Token(TokenCode.PyMul), 1),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyComma), 3),
            Pair(Token(TokenCode.PyMul), 5),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyComma), 7),
            Pair(NameToken(8, 9, "c"), 8),
            Pair(Token(TokenCode.PyRightCurly), 10),
            Pair(Token(TokenCode.EOF), 11)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(11, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(10, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(3, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is BitwiseStarExpressionNode)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] is BitwiseStarExpressionNode)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[2] is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralSingleArgument() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyRightCurly), 4),
            Pair(Token(TokenCode.EOF), 5)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(5, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(4, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralSingleArgumentWithTrailingComma() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyComma), 3),
            Pair(Token(TokenCode.PyRightCurly), 4),
            Pair(Token(TokenCode.EOF), 5)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(5, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(4, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomSetLiteralSingleArgumentWithCompIter() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyFor), 4),
            Pair(NameToken(8, 9, "b"), 8),
            Pair(Token(TokenCode.PyComma), 9),
            Pair(NameToken(10, 11, "c"), 10),
            Pair(Token(TokenCode.PyIn), 12),
            Pair(NameToken(15, 16, "d"), 15),
            Pair(Token(TokenCode.PyFor), 17),
            Pair(NameToken(21, 22, "e"), 21),
            Pair(Token(TokenCode.PyComma), 22),
            Pair(NameToken(24, 25, "f"), 24),
            Pair(Token(TokenCode.PyIn), 27),
            Pair(NameToken(30, 31, "g"), 30),
            Pair(Token(TokenCode.PyIf), 32),
            Pair(NameToken(35, 36, "h"), 35),
            Pair(Token(TokenCode.PyRightCurly), 38),
            Pair(Token(TokenCode.EOF), 39)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is SetNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as SetNode).nodeStartPos )
        assertEquals(39, ((node as EvalInputNode).rightNode as SetNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as SetNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as SetNode).rightNode is SetContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeStartPos)
        assertEquals(38, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementerSeparators!!.size)
        assertEquals(2, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes.size)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[0] is NameLiteralNode)
        assertEquals(true, (((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] is CompSyncForNode)
        assertEquals(TokenCode.PyFor, ((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).symbolOne.tokenKind )
        assertEquals(true, ((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode is ExprListNode )
        assertEquals(8, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).nodeStartPos )
        assertEquals(12, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).nodeEndPos )
        assertEquals(2, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).elementNodes.size )
        assertEquals(1, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).elementerSeparators!!.size )
        assertEquals(true, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).elementNodes[0] is NameLiteralNode )
        assertEquals(true, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).elementNodes[1] is NameLiteralNode )
        assertEquals(TokenCode.PyComma, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).leftNode as ExprListNode).elementerSeparators!![0].tokenKind )
        assertEquals(TokenCode.PyIn, ((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).symbolTwo.tokenKind )
        assertEquals(true, ((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).rightNode is NameLiteralNode )
        assertEquals(true, ((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).nextNode is CompSyncForNode )
        assertEquals(true, (((((node as EvalInputNode).rightNode as SetNode).rightNode as SetContainerNode ).elementNodes[1] as CompSyncForNode).nextNode as CompSyncForNode ).nextNode is CompIfNode )
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as SetNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralSingle() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyColon), 4),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyRightCurly), 8),
            Pair(Token(TokenCode.EOF), 9)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(9, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(8, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralSingleWithTrailingComma() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyColon), 4),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyComma), 7),
            Pair(Token(TokenCode.PyRightCurly), 8),
            Pair(Token(TokenCode.EOF), 9)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(9, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(8, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(1, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralSingleWithPower() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(Token(TokenCode.PyPower), 2),
            Pair(NameToken(4, 5, "a"), 4),
            Pair(Token(TokenCode.PyRightCurly), 6),
            Pair(Token(TokenCode.EOF), 7)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(7, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(6, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(1, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as PowerKeyNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralSingleWithAsync() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyColon), 4),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyAsync), 8),
            Pair(Token(TokenCode.PyFor), 14),
            Pair(NameToken(16, 17, "c"), 6),
            Pair(Token(TokenCode.PyIn), 18),
            Pair(NameToken(21, 22, "d"), 6),
            Pair(Token(TokenCode.PyRightCurly), 23),
            Pair(Token(TokenCode.EOF), 24)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(24, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(23, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyAsync, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as CompForNode).symbolOne.tokenKind)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralSingleWithFor() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyColon), 4),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyFor), 14),
            Pair(NameToken(16, 17, "c"), 6),
            Pair(Token(TokenCode.PyIn), 18),
            Pair(NameToken(21, 22, "d"), 6),
            Pair(Token(TokenCode.PyRightCurly), 23),
            Pair(Token(TokenCode.EOF), 24)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(24, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(23, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(0, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyFor, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as CompSyncForNode).symbolOne.tokenKind)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralMultipleElement() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyColon), 4),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyComma), 7),
            Pair(NameToken(9, 10, "c"), 9),
            Pair(Token(TokenCode.PyColon), 12),
            Pair(NameToken(13, 14, "d"), 13),
            Pair(Token(TokenCode.PyRightCurly), 15),
            Pair(Token(TokenCode.EOF), 16)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(16, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(15, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(1, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomDictionaryLiteralMultipleElementWithPowerElement() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyLeftCurly), 0),
            Pair(NameToken(2, 3, "a"), 2),
            Pair(Token(TokenCode.PyColon), 4),
            Pair(NameToken(6, 7, "b"), 6),
            Pair(Token(TokenCode.PyComma), 7),
            Pair(NameToken(9, 10, "c"), 9),
            Pair(Token(TokenCode.PyColon), 12),
            Pair(NameToken(13, 14, "d"), 13),
            Pair(Token(TokenCode.PyComma), 15),
            Pair(Token(TokenCode.PyPower), 17),
            Pair(NameToken(18, 19, "a"), 18),
            Pair(Token(TokenCode.PyRightCurly), 20),
            Pair(Token(TokenCode.EOF), 21)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is DictionaryNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as DictionaryNode).nodeStartPos )
        assertEquals(21, ((node as EvalInputNode).rightNode as DictionaryNode).nodeEndPos )
        assertEquals(TokenCode.PyLeftCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as DictionaryNode).rightNode is DictionaryContainerNode )
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeStartPos)
        assertEquals(20, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).nodeEndPos)
        assertEquals(2, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementerSeparators!!.size)
        assertEquals(3, (((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes.size)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[0] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as KeyValueNode).leftNode is NameLiteralNode)
        assertEquals(TokenCode.PyColon, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as KeyValueNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[1] as KeyValueNode).rightNode is NameLiteralNode)
        assertEquals(17, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[2] as PowerKeyNode).nodeStartPos)
        assertEquals(20, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[2] as PowerKeyNode).nodeEndPos)
        assertEquals(TokenCode.PyPower, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[2] as PowerKeyNode).symbolOne.tokenKind)
        assertEquals(true, ((((node as EvalInputNode).rightNode as DictionaryNode).rightNode as DictionaryContainerNode ).elementNodes[2] as PowerKeyNode).rightNode is NameLiteralNode)
        assertEquals(TokenCode.PyRightCurly, ((node as EvalInputNode).rightNode as DictionaryNode).symbolTwo.tokenKind )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomExprSingleAwaitAtom() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyAwait), 0),
            Pair(NameToken(6, 7, "a"), 2),
            Pair(Token(TokenCode.EOF), 7)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is AtomExpressionNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as AtomExpressionNode).nodeStartPos )
        assertEquals(7, ((node as EvalInputNode).rightNode as AtomExpressionNode).nodeEndPos )
        assertEquals(TokenCode.PyAwait, ((node as EvalInputNode).rightNode as AtomExpressionNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as AtomExpressionNode).leftNode is NameLiteralNode )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomExprSingleNameAtom() {
        val tokens = arrayOf(
            Pair(NameToken(0, 1, "a"), 0),
            Pair(Token(TokenCode.EOF), 2)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is NameLiteralNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as NameLiteralNode).nodeStartPos )
        assertEquals(2, ((node as EvalInputNode).rightNode as NameLiteralNode).nodeEndPos )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }

    @Test
    fun testAtomExprSingleAwaitAtomWithOneDotName() {
        val tokens = arrayOf(
            Pair(Token(TokenCode.PyAwait), 0),
            Pair(NameToken(6, 7, "a"), 6),
            Pair(Token(TokenCode.PyDot), 7),
            Pair(NameToken(8, 9, "b"), 8),
            Pair(Token(TokenCode.EOF), 9)
        )

        val lexer = MockedPythonCoreTokenizer(tokens)
        val parser = PythonCoreParser(lexer)
        val node = parser.parseEvalInput()
        assertEquals(true, (node is EvalInputNode))
        assertEquals(true, (node as EvalInputNode).rightNode is AtomExpressionNode)
        assertEquals(0, ((node as EvalInputNode).rightNode as AtomExpressionNode).nodeStartPos )
        assertEquals(9, ((node as EvalInputNode).rightNode as AtomExpressionNode).nodeEndPos )
        assertEquals(TokenCode.PyAwait, ((node as EvalInputNode).rightNode as AtomExpressionNode).symbolOne.tokenKind )
        assertEquals(true, ((node as EvalInputNode).rightNode as AtomExpressionNode).leftNode is NameLiteralNode )
        assertEquals(1, ((node as EvalInputNode).rightNode as AtomExpressionNode).trailerNodes!!.size)
        assertEquals(true, ((node as EvalInputNode).rightNode as AtomExpressionNode).trailerNodes!![0] is DotNameNode )
        assertEquals(TokenCode.PyDot, (((node as EvalInputNode).rightNode as AtomExpressionNode).trailerNodes!![0] as DotNameNode ).dotNode.tokenKind )
        assertEquals(TokenCode.NAME, (((node as EvalInputNode).rightNode as AtomExpressionNode).trailerNodes!![0] as DotNameNode ).nameNode.tokenKind )
        assertEquals(7, (((node as EvalInputNode).rightNode as AtomExpressionNode).trailerNodes!![0] as DotNameNode ).nodeStartPos )
        assertEquals(9, (((node as EvalInputNode).rightNode as AtomExpressionNode).trailerNodes!![0] as DotNameNode ).nodeEndPos )
        assertEquals(0, (node as EvalInputNode).newlineNode.size)
        assertEquals(TokenCode.EOF, (node as EvalInputNode).eofNode.tokenKind)
    }
}
