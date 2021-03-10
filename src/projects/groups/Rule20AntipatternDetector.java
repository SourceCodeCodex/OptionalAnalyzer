package projects.groups;

import java.util.List;
import java.util.stream.Collectors;

import optionalanalizer.metamodel.entity.MCompilationUnit;
import optionalanalizer.metamodel.entity.MProject;
import optionalanalizer.metamodel.entity.MRule20Atom;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class Rule20AntipatternDetector implements IRelationBuilder<MRule20Atom, MProject>{

	@Override
	public Group<MRule20Atom> buildGroup(MProject arg0) {
		Group<MRule20Atom> group = new Group<>();

		 List<MRule20Atom> atoms = arg0.compilationUnitDetector()
				.getElements().stream()
				.map(MCompilationUnit::rule20AntipatternDetector)
				.map(Group::getElements)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		 
		 group.addAll(atoms);
		 
		 return group;
	}
}