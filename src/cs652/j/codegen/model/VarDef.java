package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/1/2017.
 */
public class VarDef extends OutputModelObject{
    public TypeSpec typeSpec;
    public String type;
    public String ID;
    public VarDef(String t, String i) {
        type = t;
         ID = i;
    }


}
