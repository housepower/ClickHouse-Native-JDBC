package org.houseflys.jdbc.parser.ast;

public class ASTIdentifier extends AST {
    private String identifier;

    public ASTIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }
}
