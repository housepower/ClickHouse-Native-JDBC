package org.houseflys.jdbc.parser.ast;

public class ASTFunction extends AST {
    private final String name;
    // TODO: expression list
    
    public ASTFunction(String name) {
        this.name = name;
    }
}
