package net.pythoncore.parser

class SyntaxError(position: Int, message: String) : Throwable(message) {
    val positionInSource = position
}
