package cs652.j.codegen.model;

/**
 * Created by RUSHABH on 4/5/2017.
 */
public class vtable extends OutputModelObject implements Comparable<vtable>{

    public int slotNumber;
    @ModelElement public ClassName className;
    @ModelElement public FuncName functionName;
    public String name;

     @Override
    public int compareTo(vtable o) {
        return this.slotNumber-o.slotNumber;
    }
}
