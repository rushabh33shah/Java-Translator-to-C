package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class FuncName extends OutputModelObject{
    public @ModelElement String FName;
    public FuncName(String FName) {
        this.FName = FName;
    }
}