package org.houseflys.jdbc.parser;

import java.sql.SQLException;

import org.houseflys.jdbc.parser.ast.AST;
import org.houseflys.jdbc.parser.lexer.Scanner;

public abstract class Parser {
    abstract AST parseImpl(Scanner scanner) throws SQLException;

    public AST parse(Scanner scanner) throws SQLException {
        int pos = scanner.pos();
        AST res = parseImpl(scanner);

        if (res == null) {
            scanner.pos(pos);
        }
        return res;
    }
}
