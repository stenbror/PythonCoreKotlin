package net.pythoncore.Tests

import net.pythoncore.parser.*
import net.pythoncore.parser.ast.EvalInputNode
import net.pythoncore.parser.ast.NoneLiteralNode
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
}