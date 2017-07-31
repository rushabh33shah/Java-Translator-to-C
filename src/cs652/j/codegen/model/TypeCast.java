package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/6/2017.
 */
public class TypeCast extends Expr {
    @ModelElement public Expr expr;
    @ModelElement public TypeSpec type;

    public TypeCast(TypeSpec typeSpec) {
        this.type = typeSpec;
    }
}
