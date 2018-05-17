package org.houseflys.jdbc.parser;

import java.util.Arrays;
import java.util.Iterator;

import org.houseflys.jdbc.parser.lexer.Token;
import org.houseflys.jdbc.parser.lexer.TokenType;
import org.houseflys.jdbc.parser.lexer.Lexer;
import org.junit.Assert;
import org.junit.Test;

public class LexerTest {

    @Test
    public void successfully_empty_lexer() throws Exception {
        Assert.assertEquals(new Lexer("".toCharArray()).next().type(), TokenType.EndOfStream);
    }

    @Test
    public void successfully_all_tokenType() throws Exception {
        assertTokens("(", TokenType.OpeningRoundBracket);
        assertTokens(")", TokenType.ClosingRoundBracket);
        assertTokens("[", TokenType.OpeningSquareBracket);
        assertTokens("]", TokenType.ClosingSquareBracket);
        assertTokens(",", TokenType.Comma);
        assertTokens(";", TokenType.Semicolon);
        assertTokens(".", TokenType.Dot);
        assertTokens("+", TokenType.Plus);
        assertTokens("-", TokenType.Minus);
        assertTokens("--", TokenType.Comment);
        assertTokens("*", TokenType.Asterisk);
        assertTokens("/", TokenType.Slash);
        assertTokens("//comment", TokenType.Comment);
        assertTokens("/*comment*/", TokenType.Comment);
        assertTokens("%", TokenType.Percent);
        assertTokens("=", TokenType.Equals);
        assertTokens("==", TokenType.Equals);
        assertTokens("!=", TokenType.NotEquals);
        assertTokens("?", TokenType.QuestionMark);
        assertTokens(":", TokenType.Colon);
        assertTokens("||", TokenType.Concatenation);
        assertTokens("`identifier\\`\\\"\\'`", TokenType.QuotedIdentifier);
        assertTokens("\"identifier\\\"\\'\\`\"", TokenType.QuotedIdentifier);
        assertTokens("'literal\\''", TokenType.StringLiteral);
        assertTokens("abs", TokenType.BareWord);
        assertTokens("abs123", TokenType.BareWord);
        assertTokens("_abs123", TokenType.BareWord);
        assertTokens("123", TokenType.Number);
        assertTokens("0x123", TokenType.Number);
        assertTokens("0b123", TokenType.Number);
    }


    private void assertTokens(String query, TokenType... types) {
        Lexer lexer = new Lexer(query.toCharArray());

        Iterator<TokenType> it = Arrays.asList(types).iterator();

        Token token;
        while ((token = lexer.next()).type() != TokenType.EndOfStream) {
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(token.type(), it.next());
        }
    }
}