package org.houseflys.jdbc.parser.lexer;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private int pos;

    private final Lexer lexer;
    private final List<Token> tokens;

    public Scanner(String query) {
        this.lexer = new Lexer(query.toCharArray());
        this.tokens = new ArrayList<Token>();
    }

    public Token nextToken() {
        if (tokens.size() > pos) {
            return tokens.get(pos++);
        }

        pos++;
        Token next = lexer.next();
        tokens.add(next);
        return next;
    }

    public int pos() {
        return pos;
    }

    public void pos(int pos) {
        this.pos = pos;
    }
}
