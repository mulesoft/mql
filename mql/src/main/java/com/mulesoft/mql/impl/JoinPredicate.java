/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.mql.impl;

import com.mulesoft.mql.JoinBuilder;
import com.mulesoft.mql.QueryBuilder;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.mvel.MVEL;

public class JoinPredicate implements Predicate {

    private final QueryBuilder queryBuilder;
    private Serializable compiledExpression;
    private JoinBuilder join;

    public JoinPredicate(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        
        join = queryBuilder.getJoin();
        compiledExpression = MVEL.compileExpression(join.getExpression());
    }

    public boolean evaluate(Object object) {
        Map<String,Object> vars = (Map<String, Object>) object;
        
        try {
            // Execute the expression and shove the result back in as the context variable
            Object result = MVEL.executeExpression(compiledExpression, vars);
            
            vars.put(queryBuilder.getJoin().getAs(), result);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not execute expression " + join.getExpression() + " on context " + object, e);
        }
        
        return true;
    }

}