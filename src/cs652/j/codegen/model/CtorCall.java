package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class CtorCall extends Expr {

    public String name;

    public CtorCall(String name) {
        this.name = name;
    }
}
