package dev.strgzr.grump

import org.junit.Assert.*
import org.junit.Test

class ExpressionTest {
    @Test fun legacyMoodsKeepTheirSavedIds() {
        assertEquals(0, Expression.IDLE.ordinal)
        assertEquals(1, Expression.ANNOYED.ordinal)
        assertEquals(2, Expression.EXASPERATED.ordinal)
    }

    @Test fun achievementsReceiveRareReluctantAdmiration() {
        val admired = CommentEngine.comments.filter {
            Expression.forComment(it, Expression.IDLE) == Expression.IMPRESSED
        }
        assertEquals(3, admired.size)
        assertTrue(admired.all { it.category == Category.HUMANITY })
    }

    @Test fun everyCommentReactsWithoutRepeatingThePreviousPose() {
        for (previous in Expression.entries) for (comment in CommentEngine.comments) {
            assertNotEquals(previous, Expression.forComment(comment, previous))
        }
    }

    @Test fun topicsReceiveAppropriateDefaultReactions() {
        assertEquals(Expression.SUSPICIOUS, Expression.forComment(Comment(Category.SCIENCE, "test"), Expression.IDLE))
        assertEquals(Expression.ANNOYED, Expression.forComment(Comment(Category.CORPORATE, "test"), Expression.IDLE))
        assertEquals(Expression.UNIMPRESSED, Expression.forComment(Comment(Category.POLITICS, "test"), Expression.IDLE))
        assertEquals(Expression.EXASPERATED, Expression.forComment(Comment(Category.EXISTENTIAL, "test"), Expression.IDLE))
    }
}
