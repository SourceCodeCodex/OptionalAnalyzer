package compilationUnits.groups;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;

import optionalanalizer.metamodel.entity.MCompilationUnit;
import optionalanalizer.metamodel.entity.MRule13Atom;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;
import rule13Antipattern.Rule13AntipatternFinder;
import utilities.UtilityClass;

@RelationBuilder
public class Rule13AntipatternDetector implements IRelationBuilder<MRule13Atom, MCompilationUnit>{

	@Override
	public Group<MRule13Atom> buildGroup(MCompilationUnit arg0) {
		Rule13AntipatternFinder rule13AtomFinder = new Rule13AntipatternFinder();
		Group<MRule13Atom> group = new Group<>();
		ICompilationUnit iCompilationUnit = (ICompilationUnit) arg0.getUnderlyingObject();
		CompilationUnit compilationUnit = UtilityClass.parse(iCompilationUnit);
		List<MRule13Atom> atoms = rule13AtomFinder.getMAtoms(compilationUnit);
		
		group.addAll(atoms);
		return group;
	}	
}