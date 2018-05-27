package org.houseflys.jdbc.parser;

import java.sql.SQLException;

import org.houseflys.jdbc.parser.ast.AST;
import org.houseflys.jdbc.parser.ast.ASTIdentifier;
import org.houseflys.jdbc.parser.lexer.Scanner;
import org.houseflys.jdbc.parser.lexer.Token;
import org.houseflys.jdbc.parser.lexer.TokenType;

public class ParserIdentifier extends Parser {

    @Override
    AST parseImpl(Scanner scanner) throws SQLException {
        Token token = scanner.nextToken();

        int end = token.end();
        int start = token.start();
        char[] data = token.data();

        if (start < end) {
            if (token.type() == TokenType.BareWord) {
                String identifier = new String(token.data(), token.start(), token.end() - token.start());
                return new ASTIdentifier(identifier);
            } else if (token.type() == TokenType.QuotedIdentifier) {
                char quoted = data[start];
                String identifier = readQuotedString(data, start, end - start, quoted);
                return identifier.isEmpty() ? null : new ASTIdentifier(identifier);
            }
        }

        return null;
    }

    private String readQuotedString(char[] data, int offset, int end, char quoted) throws SQLException {
        if (offset + 1 < end && data[offset] == quoted) {
            for (int i = offset + 1; i < end; i++) {
                if (data[i] == '\\') {
                    i++;
                } else if (data[i] == quoted) {
                    return new String(data, offset + 1, i - (offset + 1));
                }
            }
        }
        throw new SQLException("Expected " + quoted);
    }
}
