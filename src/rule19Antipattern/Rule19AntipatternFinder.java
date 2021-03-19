package rule19Antipattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;

import optionalanalyzer.metamodel.entity.MRule19sAntipattern;
import optionalanalyzer.metamodel.factory.Factory;
import utilities.OptionalInvocationFinder;

public class Rule19AntipatternFinder {

	public List<MRule19sAntipattern> getMAntipatterns(ASTNode astNode) {
		OptionalInvocationFinder optionalInvocationFinder = new OptionalInvocationFinder();
		List<MethodInvocation> ofGroup = optionalInvocationFinder.getInvocations(astNode, "of");
		List<MethodInvocation> ofNullableGroup = optionalInvocationFinder.getInvocations(astNode, "ofNullable");
		List<MethodInvocation> badInvocationsForOptionalOf = getBadInvocationsForOptionalOf(ofGroup);
		List<MethodInvocation> badInvocationsForOptionalOfNullable = getBadInvocationsForOptionalOfNullable(ofNullableGroup);

		return Stream.of(badInvocationsForOptionalOf, badInvocationsForOptionalOfNullable)
				.flatMap(Collection::stream)
				.map(Rule19Antipattern::getInstance)
				.flatMap(Optional::stream)
				.map(Factory.getInstance()::createMRule19sAntipattern)
                .collect(Collectors.toList());
	}

	private boolean isArgumentLiteral(MethodInvocation methodInvocation) {
		List<?> arguments = methodInvocation.arguments();
		//Optional.of and ofNullable has only one argument
		if(arguments.size() != 1) {
			return false;
		}

		Object argument = arguments.get(0);

		return isLiteral((Expression)argument);
	}

	private boolean isArgumentAnOperationOfLiterals(MethodInvocation methodInvocation) {

		List<?> arguments = methodInvocation.arguments();

		if(arguments.size() != 1) {
			return false;
		}

		Object argument = arguments.get( 0 );

		boolean isArgumentAnOperantionOfLiterals = false;
		if(argument instanceof InfixExpression) {
			InfixExpression infixExpression = (InfixExpression)argument;
			List<Expression> operands = getOperands(infixExpression);
			isArgumentAnOperantionOfLiterals = !operands.stream()
					.anyMatch(expr -> !isLiteral(expr));
				
		}

		return isArgumentAnOperantionOfLiterals;
	}

	private List<Expression> getOperands(InfixExpression infixExpression) {

		List<Expression> operands = new ArrayList<>();
		operands.add(infixExpression.getLeftOperand());
		operands.add(infixExpression.getRightOperand());

		if(infixExpression.hasExtendedOperands()) {
			List<?> extendedOperands = infixExpression.extendedOperands();

			for(Object obj : extendedOperands) {
				if(obj instanceof Expression) {
					operands.add((Expression)obj);
				}
			}
		}
		return operands;
	}

	private boolean isLiteral(Expression expression) {

		return expression instanceof BooleanLiteral ||
				expression instanceof CharacterLiteral ||
				expression instanceof NullLiteral ||
				expression instanceof NumberLiteral ||
				expression instanceof StringLiteral ||
				expression instanceof TypeLiteral;
	}

	private List<MethodInvocation> getBadInvocationsForOptionalOfNullable(List<MethodInvocation> ofNullableList) {
		List<MethodInvocation> badInvocationsForOptionalOfNullable = ofNullableList.stream()
				.filter(methodInvocation -> isArgumentAnOperationOfLiterals(methodInvocation) || isArgumentLiteral(methodInvocation))
				.collect(Collectors.toList());
		
		return badInvocationsForOptionalOfNullable;
	}

	private List<MethodInvocation> getBadInvocationsForOptionalOf(List<MethodInvocation> ofList) {
		List<MethodInvocation> badInvocationsForOptionalOf = ofList.stream()
				.filter(methodInvocation -> !isArgumentLiteral(methodInvocation))
				.filter(methodInvocation -> !isArgumentAnOperationOfLiterals(methodInvocation))
				.collect(Collectors.toList());

		return badInvocationsForOptionalOf;
	}
	
}
