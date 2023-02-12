package com.studiversity.feature.role

data class ScopeType(val id: Long, val name: String, val parent: ScopeType?) {

    init {
        excludeCyclicParents()
    }

    private fun excludeCyclicParents() {
        val parentIds = mutableListOf<Long?>()

        var currentParent: ScopeType? = parent

        while (true) {
            if (currentParent == null)
                break
            if (currentParent.id in parentIds)
                throw IllegalStateException("Cyclic parent: ${currentParent}")

            parentIds.add(currentParent.id)

            currentParent = currentParent.parent
        }
    }

    companion object {
        val Organization: ScopeType =
            ScopeType(1, "organization", null)
        val User: ScopeType = ScopeType(2, "user", Organization)
        val StudyGroup: ScopeType = ScopeType(3, " group", Organization)
        val Course: ScopeType = ScopeType(4, "curse", Organization)
    }
}
