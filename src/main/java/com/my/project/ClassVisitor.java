package com.my.project;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码解析结果解析器
 * 
 * @author yang.dongdong
 */
public class ClassVisitor extends VoidVisitorAdapter < Object > {

    /**
     * 构造方法解析
     */
    @Override
    public void visit( ConstructorDeclaration n, Object o ) {
        parseAnonymous( n );
        showDeclarationInfo( n.getBeginLine(), n.getBeginColumn(), n.getEndLine(), n.getEndColumn(), parseName( n ),
                n.getName(), parseJavadoc( n ) );
    }

    /**
     * 方法解析
     */
    @Override
    public void visit( MethodDeclaration n, Object o ) {
        parseAnonymous( n );
        showDeclarationInfo( n.getBeginLine(), n.getBeginColumn(), n.getEndLine(), n.getEndColumn(), parseName( n ), n
                .getType().toString(), parseJavadoc( n ) );
    }

    /**
     * 属性解析
     */
    @Override
    public void visit( FieldDeclaration n, Object o ) {
        showDeclarationInfo( n.getBeginLine(), n.getBeginColumn(), n.getEndLine(), n.getEndColumn(), parseName( n ), n
                .getType().toString(), parseJavadoc( n ) );
    }

    /**
     * Import语句解析
     */
    @Override
    public void visit( ImportDeclaration n, Object o ) {
        showDeclarationInfo( n.getBeginLine(), n.getBeginColumn(), n.getEndLine(), n.getEndColumn(), n.toString()
                .trim(), "none", "none" );
    }

    /**
     * 匿名类解析
     */
    private void parseAnonymous( MethodDeclaration n ) {
        List < ObjectCreationExpr > expssions = new ArrayList < ObjectCreationExpr >();
        getExpressions( n.getBody(), expssions );
        for ( ObjectCreationExpr oce : expssions ) {
            showDeclarationInfo( oce.getBeginLine(), oce.getBeginColumn(), oce.getEndLine(), oce.getEndColumn(),
                    parseName( n ), "Anonymous[" + oce.getType().toString() + "]", "none" );
        }

    }

    /**
     * 匿名类解析
     */
    private void parseAnonymous( ConstructorDeclaration n ) {
        List < ObjectCreationExpr > expssions = new ArrayList < ObjectCreationExpr >();
        getExpressions( n.getBlock(), expssions );
        for ( ObjectCreationExpr oce : expssions ) {
            showDeclarationInfo( oce.getBeginLine(), oce.getBeginColumn(), oce.getEndLine(), oce.getEndColumn(),
                    parseName( n ), "Anonymous[" + oce.getType().toString() + "]", "none" );
        }
    }

    /**
     * 对源码中的表达式进行解析，提取匿名类
     * 
     * @param stmt 语句
     * @param expressions 匿名类表达式列表
     */
    private void getExpressions( Statement stmt, List < ObjectCreationExpr > expressions ) {

        if ( stmt == null || expressions == null ) {
            return;
        }

        if ( stmt instanceof AssertStmt ) {
            checkExpression( ((AssertStmt) stmt).getCheck(), expressions );
            checkExpression( ((AssertStmt) stmt).getMessage(), expressions );
            return;

        } else if ( stmt instanceof BlockStmt ) {
            if ( ((BlockStmt) stmt).getStmts() != null ) {
                for ( Statement s : ((BlockStmt) stmt).getStmts() ) {
                    getExpressions( s, expressions );
                }
            }
            return;

        } else if ( stmt instanceof BreakStmt ) {
            return;

        } else if ( stmt instanceof ContinueStmt ) {
            return;

        } else if ( stmt instanceof DoStmt ) {
            checkExpression( ((DoStmt) stmt).getCondition(), expressions );
            if ( ((DoStmt) stmt).getBody() != null ) {
                getExpressions( ((DoStmt) stmt).getBody(), expressions );
            }
            return;

        } else if ( stmt instanceof EmptyStmt ) {
            return;

        } else if ( stmt instanceof ExplicitConstructorInvocationStmt ) {
            checkExpression( ((ExplicitConstructorInvocationStmt) stmt).getExpr(), expressions );
            if ( ((ExplicitConstructorInvocationStmt) stmt).getArgs() != null
                    && ((ExplicitConstructorInvocationStmt) stmt).getArgs().size() > 0 ) {
                for ( Expression e : ((ExplicitConstructorInvocationStmt) stmt).getArgs() ) {
                    checkExpression( e, expressions );
                }
            }
            return;

        } else if ( stmt instanceof ExpressionStmt ) {
            checkExpression( ((ExpressionStmt) stmt).getExpression(), expressions );
            return;

        } else if ( stmt instanceof ForeachStmt ) {
            checkExpression( ((ForeachStmt) stmt).getIterable(), expressions );
            if ( ((ForeachStmt) stmt).getBody() != null ) {
                getExpressions( ((ForeachStmt) stmt).getBody(), expressions );
            }
            return;

        } else if ( stmt instanceof ForStmt ) {
            checkExpression( ((ForStmt) stmt).getCompare(), expressions );
            if ( ((ForStmt) stmt).getInit() != null && ((ForStmt) stmt).getInit().size() > 0 ) {
                for ( Expression e : ((ForStmt) stmt).getInit() ) {
                    checkExpression( e, expressions );
                }
            }
            if ( ((ForStmt) stmt).getUpdate() != null && ((ForStmt) stmt).getUpdate().size() > 0 ) {
                for ( Expression e : ((ForStmt) stmt).getUpdate() ) {
                    checkExpression( e, expressions );
                }
            }
            if ( ((ForStmt) stmt).getBody() != null ) {
                getExpressions( ((ForStmt) stmt).getBody(), expressions );
            }
            return;

        } else if ( stmt instanceof IfStmt ) {
            checkExpression( ((IfStmt) stmt).getCondition(), expressions );
            if ( ((IfStmt) stmt).getThenStmt() != null ) {
                getExpressions( ((IfStmt) stmt).getThenStmt(), expressions );
            }
            if ( ((IfStmt) stmt).getElseStmt() != null ) {
                getExpressions( ((IfStmt) stmt).getElseStmt(), expressions );
            }
            return;

        } else if ( stmt instanceof LabeledStmt ) {
            if ( ((LabeledStmt) stmt).getStmt() != null ) {
                getExpressions( ((LabeledStmt) stmt).getStmt(), expressions );
            }
            return;

        } else if ( stmt instanceof ReturnStmt ) {
            checkExpression( ((ReturnStmt) stmt).getExpr(), expressions );
            return;

        } else if ( stmt instanceof SwitchStmt ) {
            checkExpression( ((SwitchStmt) stmt).getSelector(), expressions );
            if ( ((SwitchStmt) stmt).getEntries() != null && ((SwitchStmt) stmt).getEntries().size() > 0 ) {
                for ( Statement s : ((SwitchStmt) stmt).getEntries() ) {
                    getExpressions( s, expressions );
                }
            }
            return;

        } else if ( stmt instanceof SwitchEntryStmt ) {
            checkExpression( ((SwitchEntryStmt) stmt).getLabel(), expressions );
            if ( ((SwitchEntryStmt) stmt).getStmts() != null && ((SwitchEntryStmt) stmt).getStmts().size() > 0 ) {
                for ( Statement s : ((SwitchEntryStmt) stmt).getStmts() ) {
                    getExpressions( s, expressions );
                }
            }
            return;

        } else if ( stmt instanceof SynchronizedStmt ) {
            checkExpression( ((SynchronizedStmt) stmt).getExpr(), expressions );
            if ( ((SynchronizedStmt) stmt).getBlock() != null ) {
                getExpressions( ((SynchronizedStmt) stmt).getBlock(), expressions );
            }
            return;

        } else if ( stmt instanceof ThrowStmt ) {
            checkExpression( ((ThrowStmt) stmt).getExpr(), expressions );
            return;

        } else if ( stmt instanceof TryStmt ) {
            if ( ((TryStmt) stmt).getTryBlock() != null ) {
                getExpressions( ((TryStmt) stmt).getTryBlock(), expressions );
            }
            if ( ((TryStmt) stmt).getCatchs() != null && ((TryStmt) stmt).getCatchs().size() > 0 ) {
                for ( CatchClause c : ((TryStmt) stmt).getCatchs() ) {
                    if ( c != null && c.getCatchBlock() != null ) {
                        getExpressions( c.getCatchBlock(), expressions );
                    }
                }
            }
            if ( ((TryStmt) stmt).getFinallyBlock() != null ) {
                getExpressions( ((TryStmt) stmt).getFinallyBlock(), expressions );
            }
            return;

        } else if ( stmt instanceof TypeDeclarationStmt ) {
            return;

        } else if ( stmt instanceof WhileStmt ) {
            checkExpression( ((WhileStmt) stmt).getCondition(), expressions );
            if ( ((WhileStmt) stmt).getBody() != null ) {
                getExpressions( ((WhileStmt) stmt).getBody(), expressions );
            }
            return;

        }
    }

    /**
     * 检查表达式是否为匿名类
     * 
     * @param e 表达式
     * @param expressions 表达式列表
     */
    private void checkExpression( Expression e, List < ObjectCreationExpr > expressions ) {
        if ( e != null && e instanceof MethodCallExpr && ((MethodCallExpr) e).getArgs() != null
                && ((MethodCallExpr) e).getArgs().size() > 0 ) {
            for ( Expression exp : ((MethodCallExpr) e).getArgs() ) {
                if ( exp != null && exp instanceof ObjectCreationExpr ) {
                    ObjectCreationExpr oce = (ObjectCreationExpr) exp;
                    if ( oce.getAnonymousClassBody() != null && oce.getAnonymousClassBody().size() > 0 ) {
                        expressions.add( oce );
                    }
                }
            }
        }
        if ( e != null && e instanceof ObjectCreationExpr && ((ObjectCreationExpr) e).getAnonymousClassBody() != null
                && ((ObjectCreationExpr) e).getAnonymousClassBody().size() > 0 ) {
            expressions.add( (ObjectCreationExpr) e );
        }
    }

    /**
     * 显示表达式信息
     * 
     * @param beginLine 开始行
     * @param beginColumn 开始列
     * @param endLine 结束行
     * @param endColumn 结束列
     * @param name 名称
     * @param type 类型
     * @param javadoc JavaDoc
     */
    private void showDeclarationInfo( int beginLine, int beginColumn, int endLine, int endColumn, String name,
            String type, String javadoc ) {
        System.out.println( String.format( "[%s,%s,%s,%s], [%s], [%s], [%s]", beginLine, beginColumn, endLine,
                endColumn, name, type, javadoc ) );
    }

    /**
     * 解析Javadoc
     * 
     * @param n 语句块定义
     * @return 语句块的Javadoc
     */
    private String parseJavadoc( Node n ) {
        return (n.getComment() != null) ? n.getComment().getContent() : "";
    }

    /**
     * 解析构造方法的名称
     * 
     * @param n 构造方法定义
     * @return 构造方法的名称
     */
    private String parseName( ConstructorDeclaration n ) {

        String name = n.getName();
        name += "(";
        String params = "";
        if ( n.getParameters() != null && n.getParameters().size() > 0 ) {
            for ( Parameter p : n.getParameters() ) {
                params = params + p.getId().getName() + ", ";
            }
        }

        if ( !"".equals( params ) ) {
            name = name + params.substring( 0, params.length() - 2 ) + ")";
        } else {
            name += ")";
        }

        return name;
    }

    /**
     * 解析方法的名称
     * 
     * @param n 方法定义
     * @return 方法的名称
     */
    private String parseName( MethodDeclaration n ) {

        String name = n.getName();
        name += "(";
        String params = "";
        if ( n.getParameters() != null && n.getParameters().size() > 0 ) {
            for ( Parameter p : n.getParameters() ) {
                params = params + p.getId().getName() + ", ";
            }
        }

        if ( !"".equals( params ) ) {
            name = name + params.substring( 0, params.length() - 2 ) + ")";
        } else {
            name += ")";
        }

        return name;
    }

    /**
     * 解析属性的名称
     * 
     * @param n 属性定义
     * @return 属性的名称
     */
    private String parseName( FieldDeclaration n ) {
        return n.getVariables().get( 0 ).getId().getName();
    }

}
