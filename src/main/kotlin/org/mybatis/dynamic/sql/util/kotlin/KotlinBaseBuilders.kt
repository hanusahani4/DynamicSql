/*
 *    Copyright 2016-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.dynamic.sql.util.kotlin

import org.mybatis.dynamic.sql.AndOrCriteriaGroup
import org.mybatis.dynamic.sql.SqlTable
import org.mybatis.dynamic.sql.configuration.StatementConfiguration
import org.mybatis.dynamic.sql.select.AbstractQueryExpressionDSL
import org.mybatis.dynamic.sql.where.AbstractWhereSupport

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@DslMarker
annotation class MyBatisDslMarker

typealias WhereApplier = KotlinBaseBuilder<*>.() -> Unit

fun WhereApplier.andThen(after: WhereApplier): WhereApplier = {
    invoke(this)
    after(this)
}

@MyBatisDslMarker
@Suppress("TooManyFunctions")
abstract class KotlinBaseBuilder<D : AbstractWhereSupport<*,*>> {

    fun configureStatement(c: StatementConfiguration.() -> Unit) {
        getDsl().configureStatement(c)
    }

    fun where(criteria: GroupingCriteriaReceiver): Unit =
        with(GroupingCriteriaCollector().apply(criteria)) {
            this@KotlinBaseBuilder.getDsl().where(initialCriterion, subCriteria)
        }

    fun where(criteria: List<AndOrCriteriaGroup>) {
        getDsl().where(criteria)
    }

    fun and(criteria: GroupingCriteriaReceiver): Unit =
        with(GroupingCriteriaCollector().apply(criteria)) {
            this@KotlinBaseBuilder.getDsl().where().and(initialCriterion, subCriteria)
        }

    fun and(criteria: List<AndOrCriteriaGroup>) {
        getDsl().where().and(criteria)
    }

    fun or(criteria: GroupingCriteriaReceiver): Unit =
        with(GroupingCriteriaCollector().apply(criteria)) {
            this@KotlinBaseBuilder.getDsl().where().or(initialCriterion, subCriteria)
        }

    fun or(criteria: List<AndOrCriteriaGroup>) {
        getDsl().where().or(criteria)
    }

    fun applyWhere(whereApplier: WhereApplier) = whereApplier.invoke(this)

    /**
     * This function does nothing, but it can be used to make some code snippets more understandable.
     *
     * For example, to count all rows in a table you can write either of the following:
     *
     * val rows = countFrom(foo) { }
     *
     *    or
     *
     * val rows = countFrom(foo) { allRows() }
     */
    @SuppressWarnings("EmptyFunctionBlock")
    fun allRows() {
        // intentionally empty - this function exists for code beautification and clarity only
    }

    protected abstract fun getDsl(): D
}

@Suppress("TooManyFunctions")
abstract class KotlinBaseJoiningBuilder<D : AbstractQueryExpressionDSL<*, *>> : KotlinBaseBuilder<D>() {

    fun join(table: SqlTable, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            join(table, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun join(table: SqlTable, alias: String, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            join(table, alias, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun join(
        subQuery: KotlinQualifiedSubQueryBuilder.() -> Unit,
        joinCriteria: JoinReceiver
    ): Unit =
        applyToDsl(subQuery, joinCriteria) { sq, jc ->
            join(sq, sq.correlationName, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun fullJoin(table: SqlTable, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            fullJoin(table, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun fullJoin(table: SqlTable, alias: String, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            fullJoin(table, alias, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun fullJoin(
        subQuery: KotlinQualifiedSubQueryBuilder.() -> Unit,
        joinCriteria: JoinReceiver
    ): Unit =
        applyToDsl(subQuery, joinCriteria) { sq, jc ->
            fullJoin(sq, sq.correlationName, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun leftJoin(table: SqlTable, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            leftJoin(table, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun leftJoin(table: SqlTable, alias: String, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            leftJoin(table, alias, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun leftJoin(
        subQuery: KotlinQualifiedSubQueryBuilder.() -> Unit,
        joinCriteria: JoinReceiver
    ): Unit =
        applyToDsl(subQuery, joinCriteria) { sq, jc ->
            leftJoin(sq, sq.correlationName, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun rightJoin(table: SqlTable, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            rightJoin(table, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun rightJoin(table: SqlTable, alias: String, joinCriteria: JoinReceiver): Unit =
        applyToDsl(joinCriteria) { jc ->
            rightJoin(table, alias, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    fun rightJoin(
        subQuery: KotlinQualifiedSubQueryBuilder.() -> Unit,
        joinCriteria: JoinReceiver
    ): Unit =
        applyToDsl(subQuery, joinCriteria) { sq, jc ->
            rightJoin(sq, sq.correlationName, jc.onJoinCriterion(), jc.andJoinCriteria)
        }

    private fun applyToDsl(joinCriteria: JoinReceiver, applyJoin: D.(JoinCollector) -> Unit) {
        getDsl().applyJoin(JoinCollector().apply(joinCriteria))
    }

    private fun applyToDsl(
        subQuery: KotlinQualifiedSubQueryBuilder.() -> Unit,
        joinCriteria: JoinReceiver,
        applyJoin: D.(KotlinQualifiedSubQueryBuilder, JoinCollector) -> Unit
    ) {
        getDsl().applyJoin(KotlinQualifiedSubQueryBuilder().apply(subQuery), JoinCollector().apply(joinCriteria))
    }
}
