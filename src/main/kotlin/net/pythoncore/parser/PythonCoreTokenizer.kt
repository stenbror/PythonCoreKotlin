package net.pythoncore.parser

class PythonCoreTokenizer(text: String) {
    private val sourceBuffer = text

    var curSymbol: Token = Token(TokenCode.Empty)
    var curIndex: Int = 0


    fun advance() : Unit { }
}