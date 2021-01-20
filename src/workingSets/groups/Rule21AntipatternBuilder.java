package workingSets.groups;

import java.util.List;
import java.util.stream.Collectors;

import optionalanalizer.metamodel.entity.MProject;
import optionalanalizer.metamodel.entity.MRule21Atom;
import optionalanalizer.metamodel.entity.MWorkingSet;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class Rule21AntipatternBuilder implements IRelationBuilder<MRule21Atom, MWorkingSet>{

	@Override
	public Group<MRule21Atom> buildGroup(MWorkingSet arg0) {
		Group<MRule21Atom> group = new Group<>();

		 List<MRule21Atom> atoms = arg0.getComponentProjects()
				.getElements().stream()
				.map(MProject::rule21AntipatternBuilder)
				.map(Group::getElements)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		 
		 group.addAll(atoms);
		 
		 return group;
	}

}