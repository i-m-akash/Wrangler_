package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.LazyNumber;
import io.cdap.wrangler.api.RecipeSymbol;
import io.cdap.wrangler.api.SourceInfo;
import io.cdap.wrangler.api.Triplet;
import io.cdap.wrangler.api.parser.Bool;
import io.cdap.wrangler.api.parser.BoolList;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.ColumnNameList;
import io.cdap.wrangler.api.parser.DirectiveName;
import io.cdap.wrangler.api.parser.Expression;
import io.cdap.wrangler.api.parser.Identifier;
import io.cdap.wrangler.api.parser.Numeric;
import io.cdap.wrangler.api.parser.NumericList;
import io.cdap.wrangler.api.parser.Properties;
import io.cdap.wrangler.api.parser.Ranges;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TextList;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.Token;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class <code>RecipeVisitor</code> implements the visitor pattern
 * used during traversal of the AST tree. The <code>ParserTree#Walker</code>
 * invokes appropriate methods as callbacks with information about the node.
 */
public final class RecipeVisitor extends DirectivesBaseVisitor<RecipeSymbol.Builder> {
    private RecipeSymbol.Builder builder = new RecipeSymbol.Builder();

    /**
     * Returns a <code>RecipeSymbol</code> for the recipe being parsed.
     */
    public RecipeSymbol getCompiledUnit() {
        return builder.build();
    }

    @Override
    public RecipeSymbol.Builder visitDirective(DirectivesParser.DirectiveContext ctx) {
        builder.createTokenGroup(getOriginalSource(ctx));
        return super.visitDirective(ctx);
    }

    @Override
    public RecipeSymbol.Builder visitIdentifier(DirectivesParser.IdentifierContext ctx) {
        builder.addToken(new Identifier(ctx.Identifier().getText()));
        return super.visitIdentifier(ctx);
    }

    @Override
    public RecipeSymbol.Builder visitPropertyList(DirectivesParser.PropertyListContext ctx) {
        Map<String, Token> props = new HashMap<>();
        for (DirectivesParser.PropertyContext property : ctx.property()) {
            String identifier = property.Identifier().getText();
            Token token = parseProperty(property);
            props.put(identifier, token);
        }
        builder.addToken(new Properties(props));
        return builder;
    }

    private Token parseProperty(DirectivesParser.PropertyContext property) {
        if (property.number() != null) {
            return new Numeric(new LazyNumber(property.number().getText()));
        } else if (property.bool() != null) {
            return new Bool(Boolean.valueOf(property.bool().getText()));
        } else {
            String text = property.text().getText();
            return new Text(text.substring(1, text.length() - 1));
        }
    }

    @Override
    public RecipeSymbol.Builder visitPragmaLoadDirective(DirectivesParser.PragmaLoadDirectiveContext ctx) {
        for (TerminalNode identifier : ctx.identifierList().Identifier()) {
            builder.addLoadableDirective(identifier.getText());
        }
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitPragmaVersion(DirectivesParser.PragmaVersionContext ctx) {
        builder.addVersion(ctx.Number().getText());
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitNumberRanges(DirectivesParser.NumberRangesContext ctx) {
        List<Triplet<Numeric, Numeric, String>> output = new ArrayList<>();
        for (DirectivesParser.NumberRangeContext range : ctx.numberRange()) {
            List<TerminalNode> numbers = range.Number();
            String text = range.value().getText();
            if (text.startsWith("'") && text.endsWith("'")) {
                text = text.substring(1, text.length() - 1);
            }
            output.add(new Triplet<>( 
                new Numeric(new LazyNumber(numbers.get(0).getText())),
                new Numeric(new LazyNumber(numbers.get(1).getText())),
                text
            ));
        }
        builder.addToken(new Ranges(output));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitEcommand(DirectivesParser.EcommandContext ctx) {
        builder.addToken(new DirectiveName(ctx.Identifier().getText()));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitColumn(DirectivesParser.ColumnContext ctx) {
        builder.addToken(new ColumnName(ctx.Column().getText().substring(1)));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitText(DirectivesParser.TextContext ctx) {
        String value = ctx.String().getText();
        builder.addToken(new Text(value.substring(1, value.length() - 1)));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitNumber(DirectivesParser.NumberContext ctx) {
        LazyNumber number = new LazyNumber(ctx.Number().getText());
        builder.addToken(new Numeric(number));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitBool(DirectivesParser.BoolContext ctx) {
        builder.addToken(new Bool(Boolean.valueOf(ctx.Bool().getText())));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitCondition(DirectivesParser.ConditionContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < ctx.getChildCount() - 1; ++i) {
            sb.append(ctx.getChild(i).getText()).append(" ");
        }
        builder.addToken(new Expression(sb.toString()));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitCommand(DirectivesParser.CommandContext ctx) {
        builder.addToken(new DirectiveName(ctx.Identifier().getText()));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitColList(DirectivesParser.ColListContext ctx) {
        List<String> names = new ArrayList<>();
        for (TerminalNode column : ctx.Column()) {
            names.add(column.getText().substring(1));
        }
        builder.addToken(new ColumnNameList(names));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitNumberList(DirectivesParser.NumberListContext ctx) {
        List<LazyNumber> numerics = new ArrayList<>();
        for (TerminalNode number : ctx.Number()) {
            numerics.add(new LazyNumber(number.getText()));
        }
        builder.addToken(new NumericList(numerics));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitBoolList(DirectivesParser.BoolListContext ctx) {
        List<Boolean> booleans = new ArrayList<>();
        for (TerminalNode bool : ctx.Bool()) {
            booleans.add(Boolean.parseBoolean(bool.getText()));
        }
        builder.addToken(new BoolList(booleans));
        return builder;
    }

    @Override
    public RecipeSymbol.Builder visitStringList(DirectivesParser.StringListContext ctx) {
    List<TerminalNode> strings = ctx.String();
    List<String> strs = new ArrayList<>();
    for (TerminalNode string : strings) {
      String text = string.getText();
      strs.add(text.substring(1, text.length() - 1));
    }
    builder.addToken(new TextList(strs));
    return builder;
  }

  @Override
public RecipeSymbol.Builder visitValue(DirectivesParser.ValueContext ctx) {
    String text = ctx.getText();

    if (text.matches("(?i)[0-9]+(\\.[0-9]+)?(KB|MB|GB|TB)")) {
        builder.addToken(new ByteSize(text));
    } else if (text.matches("[0-9]+(\\.[0-9]+)?(ms|s|m|h)")) {
        builder.addToken(new TimeDuration(text));
    } else {
        // fallback case: treat as plain text (optional)
        builder.addToken(new Text(text));
    }

    return builder;
}



    private SourceInfo getOriginalSource(ParserRuleContext ctx) {
        int a = ctx.getStart().getStartIndex();
        int b = ctx.getStop().getStopIndex();
        Interval interval = new Interval(a, b);
        String text = ctx.start.getInputStream().getText(interval);
        int lineno = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new SourceInfo(lineno, column, text);
    }
}

