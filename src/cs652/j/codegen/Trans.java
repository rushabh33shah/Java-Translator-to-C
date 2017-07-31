package cs652.j.codegen;

/**
 * Created by PriyamPatel on 02/04/17.
 */
import cs652.j.codegen.model.OutputModelObject;
import cs652.j.parser.JLexer;
import cs652.j.parser.JParser;
import cs652.j.semantics.ComputeTypes;
import cs652.j.semantics.DefineScopesAndSymbols;
import org.antlr.symtab.GlobalScope;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;

public class Trans {
    public static GlobalScope globals = new GlobalScope(null);
    public static void main(String[] args) {
        String code =
                "class Employee {\n" +
                        "    int ID;\n" +
                        "    int getID() { return ID; }\n" +
                        "    void setID(int ID) { this.ID = ID; }\n" +
                        "}\n" +
                        "class Mgr extends Employee {\n" +
                        "    int level;\n" +
                        "}\n" +
                        "class Coder extends Employee {\n" +
                        "    float salary;\n" +
                        "    Mgr boss;\n" +
                        "    void raise(float v) { salary = v; }\n" +
                        "    void speak() { printf(\"I am %d\\n\", ID); }\n" +
                        "    void workFor(Employee e) { boss=e; }\n" +
                        "}\n" +
                        "int ID;\n" +
                        "ID = 1;\n" +
                        "Coder c;\n" +
                        "c = new Coder();\n" +
                        "c.ID = ID;\n" +
                        "c.boss = new Mgr();\n" +
                        "c.boss.level = 99;\n" +
                        "c.boss.ID = 4;\n" +
                        "printf(\"%d\\n\", c.boss.getID());\n" +
                        "c.speak();\n\n";
        ANTLRInputStream input = new ANTLRInputStream(code);
        JLexer lexer = new JLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JParser parser = new JParser(tokens);
        ParseTree tree = parser.file(); // start up

        System.out.println(tree.toStringTree(parser));

        DefineScopesAndSymbols def = new DefineScopesAndSymbols(globals);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(def, tree);

        ComputeTypes computeTypes = new ComputeTypes(globals);
        walker = new ParseTreeWalker();
        walker.walk(computeTypes, tree);

        CodeGenerator gen = new CodeGenerator(code);
        OutputModelObject file = gen.visit(tree);

        ModelConverter converter = new ModelConverter(CodeGenerator.templates);
        ST output = converter.walk(file);
        System.out.println(output.render());
    }
}