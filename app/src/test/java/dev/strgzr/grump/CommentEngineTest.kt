package dev.strgzr.grump

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class CommentEngineTest {
    @Test fun libraryHas100UniqueCompactCommentsAcrossAllCategories() {
        val comments = CommentEngine.comments
        assertEquals(100, comments.size)
        assertEquals(100, comments.map { it.text }.toSet().size)
        assertEquals(Category.entries.toSet(), comments.map { it.category }.toSet())
        assertTrue(comments.all { it.text.isNotBlank() && it.text.length <= 105 })
    }

    @Test fun everyPreviousEntryIsExcludedAndAllOthersAreReachable() {
        for (previous in CommentEngine.comments.indices) {
            val random = Random(previous)
            val results = (1..4000).map { CommentEngine.nextIndex(previous, random) }.toSet()
            assertFalse(results.contains(previous))
            assertEquals(CommentEngine.comments.indices.toSet() - previous, results)
        }
    }

    @Test fun missingOrStaleSavedIndexStillProducesValidComment() {
        for (previous in listOf(-1, Int.MIN_VALUE, 100, Int.MAX_VALUE)) {
            assertTrue(CommentEngine.nextIndex(previous, Random(42)) in CommentEngine.comments.indices)
        }
    }
}
