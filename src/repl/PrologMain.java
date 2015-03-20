package com.googlecode.prolog_cafe.repl;
import com.googlecode.prolog_cafe.exceptions.HaltException;
import com.googlecode.prolog_cafe.lang.ListTerm;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.StructureTerm;
import com.googlecode.prolog_cafe.lang.SymbolTerm;
import com.googlecode.prolog_cafe.lang.Term;

import java.util.EnumSet;
import java.util.StringTokenizer;
/**
 * Prolog Cafe launcher.
 * The <code>PrologMain</code> class launchs the Prolog Cafe system.<br>
 * The usage is as follows, where 
 * <code>package</code> is a package name, and 
 * <code>predicate</code> is a predicate name (only atom).
 * <pre>
 *   % java -cp $PLCAFEDIR/plcafe.jar com.googlecode.prolog_cafe.lang.PrologMain package:predicate
 *   % java -cp $PLCAFEDIR/plcafe.jar com.googlecode.prolog_cafe.lang.PrologMain predicate
 * </pre>
 * Let us show a sample session for launching a small Prolog interpreter:
 * <code>com.googlecode.prolog_cafe.builtin:cafeteria/0</code>.<br>
 * <pre>
 *    % java  -cp $PLCAFEDIR/plcafe.jar:$CLASSPATH com.googlecode.prolog_cafe.lang.PrologMain com.googlecode.prolog_cafe.builtin:cafeteria
 *    Prolog Cafe X.X.X (YYY)
 *    Copyright(C) 1997-200X M.Banbara and N.Tamura
 *    | ?- 
 * </pre>
 *
 * @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 * @version 1.0
 */
public class PrologMain {
    private static final String COPYRIGHT = "Copyright(C) 1997-2009 M.Banbara and N.Tamura";
    private static final String HEADER = "Prolog Cafe (" + COPYRIGHT + ")";

    public static void main(String argv[]) {
	BlockingPrologControl p;
	try {
	    System.err.println(HEADER);
	    if (argv.length != 1) {
		usage();
		System.exit(1);
	    } 
	    Term arg1 = Prolog.Nil;
	    arg1 = new ListTerm(SymbolTerm.intern("user"), arg1);
	    arg1 = new ListTerm(SymbolTerm.intern(Prolog.BUILTIN), arg1);
	    Term arg2 = parseAtomicGoal(argv[0]);
	    if (arg2  == null) {
		usage();
		System.exit(1);
	    }

	    p = new BlockingPrologControl();
	    p.setEnabled(EnumSet.allOf(Prolog.Feature.class), true);
	    p.configureUserIO(System.in, System.out, System.err);
	    p.setPredicate(Prolog.BUILTIN, "initialization", arg1, arg2);
	    for (boolean r = p.call(); r; r = p.redo()) {}
	    System.exit(0);
	} catch (HaltException e) {
	    System.exit(e.getStatus());
	} catch (Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    /** Returns a term for given string representation of atom goal, or 
     * <code>null</code> if parsing fails.
     * @param s  a string representation of initial goal (ex. foge:main).
     * @return a term which corresponds to a given string,
     * or <code>null</code> if parsing fails.
     */
    protected static Term parseAtomicGoal(String s) {
	StringTokenizer st = new StringTokenizer(s, ":");
	int i = st.countTokens();
	if (i == 1) {
	    Term[] args = {SymbolTerm.intern("user"), 
			   SymbolTerm.create(st.nextToken())};
	    return new StructureTerm(SymbolTerm.intern(":", 2), args);
	} else if (i == 2) {
	    Term[] args = {SymbolTerm.create(st.nextToken()), 
			   SymbolTerm.create(st.nextToken())};
	    return new StructureTerm(SymbolTerm.intern(":", 2), args);
	} else {
	    return null;
	}
    }

  /** Shows usage */
  protected static void usage() {
    System.err.println("Usage:");
    System.err.println("  java -jar cafeteria.jar package:predicate");
    System.err.println("  java -jar cafeteria.jar predicate");
    System.err.println("    package:        package name\n");
    System.err.println("    predicate:      predicate name (only atom)");
  }
}
