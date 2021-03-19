package rule_3Antipattern;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;

import optionalanalyzer.metamodel.entity.MRule3sAntipattern;
import optionalanalyzer.metamodel.factory.Factory;
import utilities.OptionalInvocationFinder;
import utilities.ToolBoxForIfStatementAnalysis;
import utilities.Unit;
import utilities.UtilityClass;

public class Rule3AntipatternFinder{

	public List<MRule3sAntipattern> getMAntipatterns(ASTNode astNode) {
		return getAntipatterns(astNode)
				.stream()
				.map(Rule3Antipattern::getInstance)
				.flatMap(Optional::stream)
				.map(Factory.getInstance()::createMRule3sAntipattern)
				.collect(Collectors.toList());
	}

	private List<IfStatement> getAntipatterns(ASTNode astNode) {
		OptionalInvocationFinder optionalInvocationFinder = new OptionalInvocationFinder();
		List<MethodInvocation> invocations = optionalInvocationFinder.getInvocations(astNode);

		return collectAntipatterns(invocations);
	}

	private List<IfStatement> collectAntipatterns(List<MethodInvocation> invocations) {

		return invocations.stream()
				.map(this::getParentIfStatementIfProblematic)
				.flatMap(Optional::stream)
				.collect(Collectors.toList());
	}

	private Optional<IfStatement> getParentIfStatementIfProblematic(MethodInvocation methodInvocation) {

		if(ToolBoxForIfStatementAnalysis.isSuperParentIfStatement(methodInvocation)) {
			final IfStatement ifStatement = ToolBoxForIfStatementAnalysis.getIfStatement(methodInvocation);
			Optional<String> invocatorName = UtilityClass.getInvocatorName(methodInvocation);
			return invocatorName.filter(invName -> isAntipattern(ifStatement, invName))
					.map(invName -> ifStatement);
		}
		return Optional.empty();
	}

	private boolean isCyclomaticComplexityForBothOne(Statement thenStatement, Statement elseStatement) {
		return ToolBoxForIfStatementAnalysis.getCyclomaticComplexity(thenStatement) == 1 
				&& ToolBoxForIfStatementAnalysis.getCyclomaticComplexity(elseStatement) == 1;
	}

	private boolean isStatementComposedByASingleActionBorBoth(Statement thenStatement, Statement elseStatement) {
		return ToolBoxForIfStatementAnalysis.isStatementComposedByASimgleAction(thenStatement)
				&& ToolBoxForIfStatementAnalysis.isStatementComposedByASimgleAction(elseStatement);
	}


	private  boolean isAntipattern(IfStatement ifStatement, String invocatorName) {
		Optional<Statement> thenStatement = Optional.ofNullable(ifStatement.getThenStatement());
		Optional<Statement> elseStatement = Optional.ofNullable(ifStatement.getElseStatement());

		return thenStatement.flatMap(
				thenStm -> elseStatement.filter(elseStm -> isCyclomaticComplexityForBothOne(thenStm, elseStm))
				.filter(elseStm -> isStatementComposedByASingleActionBorBoth(thenStm, elseStm))
				.map(elseStm -> isAntipattern(thenStm, elseStm,  invocatorName))
				).orElse(false);

	}

	private boolean isAntipattern(Statement thenStatement, Statement elseStatement, String invocatorName) {
		Optional<ReturnStatement> returnStatementForThen = ToolBoxForIfStatementAnalysis.getReturnStatement(thenStatement);
		Optional<ReturnStatement> returnStatementForElse = ToolBoxForIfStatementAnalysis.getReturnStatement(elseStatement);

		return returnStatementForThen.flatMap(
				retStmForThen -> returnStatementForElse.map(retStmForElse -> isAntipattern(retStmForThen, retStmForElse, invocatorName)
						)
				).orElse(false);
	}


	private boolean isAntipattern(ReturnStatement returnStatementForThen, ReturnStatement returnStatementForElse, String invocatorName) {
		return ToolBoxForIfStatementAnalysis.containsGetFromOptional(returnStatementForThen, invocatorName)
				&& !ToolBoxForIfStatementAnalysis.containsMethodInvocation(returnStatementForElse) 
				|| ToolBoxForIfStatementAnalysis.containsGetFromOptional(returnStatementForElse, invocatorName)
				&& !ToolBoxForIfStatementAnalysis.containsMethodInvocation(returnStatementForThen);
	}

}
