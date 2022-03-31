package com.yourorganization.maven_sample;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import java.nio.file.Paths;

/**
 * Some code that uses JavaParser.
 */
public class LogicPositivizer {
    public static void main(String[] args) {
        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
         int i=1000;
         String t="talk";
        
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        
        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(LogicPositivizer.class).resolve("src/main/resources"));

        // Our sample is in the root of this directory, so no package name.
        CompilationUnit cu = sourceRoot.parse("", "Blabla.java");

        Log.info("Positivizing!");
        
        cu.accept(new ModifierVisitor<Void>() {
            /**
             * For every if-statement, see if it has a comparison using "!=".
             * Change it to "==" and switch the "then" and "else" statements around.
             */
            @Override
            public Visitable visit(IfStmt n, Void arg) {
                // Figure out what to get and what to cast simply by looking at the AST in a debugger! 
                n.getCondition().ifBinaryExpr(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                        /* It's a good idea to clone nodes that you move around.
                            JavaParser (or you) might get confused about who their parent is!
                        */
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);
                        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                    }
                });
                return super.visit(n, arg);
            }
        }, null);

        // This saves all the files we just read to an output directory.  
        sourceRoot.saveAll(
                // The path of the Maven module/project which contains the LogicPositivizer class.
                CodeGenerationUtils.mavenModuleRoot(LogicPositivizer.class)
                        // appended with a path to "output"
                        .resolve(Paths.get("output")));
        /*A fully working sample Maven project that parses and generates code with JavaParser

This is targeted at people without Maven experience.

To build it, you will need to download and unpack the latest (or recent) version of Maven (https://maven.apache.org/download.cgi) and put the mvn command on your path. Then, you will need to install a Java 1.8 (or higher) JDK (not JRE!), and make sure you can run java from the command line. Now you can run mvn clean install and Maven will compile your project, an put the results it in two jar files in the target directory. If you like to run from the command line, execute java -jar target/javaparser-maven-sample-1.0-SNAPSHOT-shaded.jar.

How you run this code is up to you, but usually you would start by using an IDE like NetBeans, Intellij IDEA, or Eclipse.

The Maven dependencies may lag behind the official releases a bit.

If you notice some problems with this setup, please open an issue. */
    }
    public void p(){
        Log.info("printing");
    }

}
