package workingSets.groups;

import java.util.List;
import java.util.stream.Collectors;

import optionalanalizer.metamodel.entity.MCompilationUnit;
import optionalanalizer.metamodel.entity.MProject;
import optionalanalizer.metamodel.entity.MRule1Atom;
import optionalanalizer.metamodel.entity.MRule20Atom;
import optionalanalizer.metamodel.entity.MWorkingSet;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class Rule20AntipatternBuilder implements IRelationBuilder<MRule20Atom, MWorkingSet>{

	@Override
	public Group<MRule20Atom> buildGroup(MWorkingSet arg0) {
		Group<MRule20Atom> group = new Group<>();

		 List<MRule20Atom> atoms = arg0.getComponentProjects()
				.getElements().stream()
				.map(MProject::rule20AntipatternBuilder)
				.map(Group::getElements)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		 
		 group.addAll(atoms);
		 
		 return group;
	}

}