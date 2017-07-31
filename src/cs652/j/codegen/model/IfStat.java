package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/2/2017.
 */
public class IfStat extends Stat {

    public @ModelElement Expr condition;
    public @ModelElement Stat stat;
}
