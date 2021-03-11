package rule26Antipattern;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrefixExpression;

import optionalanalizer.metamodel.entity.MRule26sAntipattern;
import optionalanalizer.metamodel.factory.Factory;
import utilities.OptionalInvocationFinder;

public class Rule26AntipatternFinder {
	
	public List<MRule26sAntipattern> getMAntipatterns(ASTNode astNode) {
		OptionalInvocationFinder optionalInvocationFinder = new OptionalInvocationFinder();
		List<MethodInvocation> isPresentInvocations = optionalInvocationFinder.getInvocations(astNode);
		
		return isPresentInvocations.stream()
				.map(this::getParentPrefixExpression)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(this::isOperatorNegation)
				.map(Rule26Antipattern::getInstance)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(Factory.getInstance()::createMRule26sAntipattern)
				.collect(Collectors.toList());
	} 

	private Optional<PrefixExpression> getParentPrefixExpression(MethodInvocation methodInvocation) {
		ASTNode astNode = methodInvocation;

		while(astNode != null && !(astNode instanceof PrefixExpression)) {
			astNode = astNode.getParent();
		}

		return Optional.ofNullable((PrefixExpression)astNode);
	}

	private boolean isOperatorNegation(PrefixExpression prefixExpression) {
		String stringForm = prefixExpression.getOperator().toString();
		PrefixExpression.Operator operator = PrefixExpression.Operator.toOperator(stringForm);

		return operator == PrefixExpression.Operator.NOT;
	}

}
