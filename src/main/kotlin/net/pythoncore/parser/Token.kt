package net.pythoncore.parser

open class Token(tokenCode: TokenCode, startPos: Int = -1, endPos: Int = -1) {
    val tokenKind = tokenCode
    val startPosition = startPos
    val endPosition = endPos
}

class NameToken(startPos: Int, endPos: Int, text: String) : Token(TokenCode.NAME, startPos, endPos) {
    val textData = text
}