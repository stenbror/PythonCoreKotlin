package net.pythoncore.parser

interface IPythonCoreTokenizer {
    var curSymbol: Token
    var curIndex: Int
    fun advance() : Unit
}

class PythonCoreTokenizer(text: String) : IPythonCoreTokenizer {
    private val sourceBuffer = text

    override var curSymbol: Token = Token(TokenCode.Empty)
    override var curIndex: Int = 0


    override fun advance() : Unit { }
}