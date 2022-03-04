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
}
