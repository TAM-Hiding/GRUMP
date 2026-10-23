package dev.strgzr.grump

/** Keep the first three ordinals stable so v0.1 saved moods survive upgrades. */
enum class Expression(val description: String) {
    IDLE("exhausted"), ANNOYED("mildly irritated"), EXASPERATED("sighing"),
    SUSPICIOUS("skeptical"), IMPRESSED("reluctantly impressed"), UNIMPRESSED("disappointed");

    companion object {
        private val achievements = setOf(
            "The species persists. Occasionally it even holds a door.",
            "A stranger returned a lost wallet. Annoyingly good work.",
            "You made soup for someone. Fine. Civilization gets another day."
        )

        fun forComment(comment: Comment, previous: Expression): Expression {
            val preferred = if (comment.text in achievements) IMPRESSED else when (comment.category) {
                Category.TECHNOLOGY, Category.SCIENCE -> SUSPICIOUS
                Category.CORPORATE -> ANNOYED
                Category.POLITICS, Category.HUMANITY -> UNIMPRESSED
                Category.EXISTENTIAL -> EXASPERATED
            }
            // A poke should always look like a reaction, even within the same category.
            return if (preferred != previous) preferred
                else if (preferred != EXASPERATED) EXASPERATED else UNIMPRESSED
        }
    }
}
