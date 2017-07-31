package cs652.j.semantics;

import org.antlr.symtab.ParameterSymbol;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Created by RUSHABH on 3/3/2017.
 */
public class JArg extends ParameterSymbol {


    public JArg(String name) {
        super(name);
    }

    public JArg(String name, ParserRuleContext tree) {
        super(name);
        setDefNode(tree);
    }
}
