package com.googlecode.prolog_cafe.builtin;
import  com.googlecode.prolog_cafe.lang.*;
import java.lang.reflect.*;
/**
 * <code>'$call'/2</code><br>
 * @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 * @version 1.0
 */
class PRED_$call_2 extends Predicate.P2 {
    private static final SymbolTerm SYM_SLASH_2 = SymbolTerm.makeSymbol("/", 2);

    public PRED_$call_2(Term a1, Term a2, Operation cont) {
	arg1 = a1;
	arg2 = a2;
	this.cont = cont;
    }

    public Operation exec(Prolog engine) {
        engine.setB0();
	Term a1, a2;
	a1 = arg1.dereference(); // a1 must be atom of package name
	a2 = arg2.dereference(); // a2 must be callable name

	String functor;
	int arity;
	Term[] args;
	Class clazz;
	Constructor constr;
	Operation pred;

	try {
	    if (! a1.isSymbol())
		throw new IllegalTypeException(this, 1, "atom", a1);
	    if (a2.isSymbol()) {
		functor = ((SymbolTerm)a2).name();
		args    = new Term[] {};
		arity   = 0;
	    } else if (a2.isStructure()) {
		functor = ((StructureTerm)a2).functor().name();
		args    = ((StructureTerm)a2).args();
		arity   = ((StructureTerm)a2).arity();
	    } else {
		throw new IllegalTypeException(this, 2, "callable", a2);
	    }
	    try {
	      return engine.pcl.predicate(((SymbolTerm)a1).name(), functor, cont, args);
	    } catch (ExistenceException e) {
		try {
            return engine.pcl.predicate("com.googlecode.prolog_cafe.builtin", functor, cont, args);
		} catch (ExistenceException ee) {
		    if ((engine.getUnknown()).equals("fail"))
			return engine.fail();
		    Term[] fa = {SymbolTerm.makeSymbol(functor), new IntegerTerm(arity)};
		    throw new ExistenceException(this, 0, "procedure", new StructureTerm(SYM_SLASH_2, fa), "");
		}
	    }
	} catch (IllegalArgumentException e) {
	    throw new SystemException(e.toString() + " in " + this.toString());
	}
    }
}


