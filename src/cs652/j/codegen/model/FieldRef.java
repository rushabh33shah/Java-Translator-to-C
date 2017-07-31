package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class FieldRef extends Expr{
    public String name;
    @ModelElement public Expr object;

    public FieldRef(String name) {
        this.name = name;
    }
}
