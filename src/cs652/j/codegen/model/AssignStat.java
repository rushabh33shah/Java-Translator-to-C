package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUSHABH on 4/1/2017.
 */
public class AssignStat extends Stat {

    public @ModelElement Expr left;
    public @ModelElement Expr right;
    public String rightString;
   // public @ModelElement OutputModelObject ctorCall;

   // public @ModelElement
  //  List<OutputModelObject> idRef = new ArrayList();

   /* public AssignStat(String l, String r) {
        right=r;
        left=l;
    }*/
}
