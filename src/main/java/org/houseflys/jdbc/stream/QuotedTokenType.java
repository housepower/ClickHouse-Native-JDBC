package org.houseflys.jdbc.stream;

public enum QuotedTokenType {
    OpeningRoundBracket, ClosingRoundBracket, OpeningSquareBracket, ClosingSquareBracket, Comma, Semicolon, QuestionMark, Whitespace, Number, StringLiteral, BareWord, EndOfStream, Error, Equals;
}
