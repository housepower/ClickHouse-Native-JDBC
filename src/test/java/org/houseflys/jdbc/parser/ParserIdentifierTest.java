package org.houseflys.jdbc.parser;

import org.houseflys.jdbc.parser.ast.AST;
import org.houseflys.jdbc.parser.ast.ASTIdentifier;
import org.houseflys.jdbc.parser.lexer.Scanner;
import org.junit.Assert;
import org.junit.Test;

public class ParserIdentifierTest {

    @Test
    public void successfullyParserBareWordIdentifier() throws Exception {
        AST identifier = new ParserIdentifier().parse(new Scanner("identifier"));

        Assert.assertNotNull(identifier);
        Assert.assertTrue(identifier instanceof ASTIdentifier);
        Assert.assertEquals(((ASTIdentifier) identifier).identifier(), "identifier");
    }

    @Test
    public void successfullyParserQuotedIdentifier() throws Exception {
        AST identifier = new ParserIdentifier().parse(new Scanner("`identifier`"));

        Assert.assertNotNull(identifier);
        Assert.assertTrue(identifier instanceof ASTIdentifier);
        Assert.assertEquals(((ASTIdentifier) identifier).identifier(), "identifier");
    }
}