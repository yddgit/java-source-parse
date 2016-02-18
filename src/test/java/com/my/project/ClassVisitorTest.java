package com.my.project;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

public class ClassVisitorTest {

    public static void main( String[] args ) throws IOException, ParseException {
        CompilationUnit cu = JavaParser.parse( new File( "src/test/java/com/my/project/MyClass.java" ) );
        new ClassVisitor().visit( cu, null );
    }
}
