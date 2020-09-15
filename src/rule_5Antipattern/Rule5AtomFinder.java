package rule_5Antipattern;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

import optionalanalizer.metamodel.entity.MRule5Atom;
import optionalanalizer.metamodel.factory.Factory;
import rule_4Antipattern.Rule4Atom;
import utilities.OptionalInvocationFinder;
import utilities.baseComponentsForRules3_4_5_6_8_9.Antipattern;
import utilities.baseComponentsForRules3_4_5_6_8_9.AntipatternFinderInIfStatements;

public class Rule5AtomFinder extends AntipatternFinderInIfStatements {
	public List<MRule5Atom> getMAtoms(ASTNode astNode) {
		return getAtoms(astNode)
				.stream()
				.map(Rule5Atom::getInstance)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(el -> Factory.getInstance().createMRule5Atom(el))
				.collect(Collectors.toList());
	}

	private List<IfStatement> getAtoms(ASTNode astNode) {
		OptionalInvocationFinder optionalInvocationFinder = new OptionalInvocationFinder();
		List<MethodInvocation> invocations = optionalInvocationFinder.getInvocations(astNode);
		return collectAntipatterns(invocations);
	}

	private List<IfStatement> collectAntipatterns(List<MethodInvocation> invocations) {
		return collectAntipatterns(invocations, Antipattern.RULE_5_ANTIPATTERN);
	}
}