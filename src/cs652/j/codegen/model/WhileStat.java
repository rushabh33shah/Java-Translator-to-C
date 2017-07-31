package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/2/2017.
 */
public class WhileStat extends Stat {

    public @ModelElement Expr condition;
    //public @ModelElement String stat;
    public @ModelElement OutputModelObject stat;
}
