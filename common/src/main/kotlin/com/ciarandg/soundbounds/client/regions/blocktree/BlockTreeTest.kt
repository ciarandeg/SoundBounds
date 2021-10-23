package com.ciarandg.soundbounds.client.regions.blocktree

import net.minecraft.util.math.BlockPos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class BlockTreeTest {

    @Test
    fun getSize() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        assertFalse(tree.contains(pos1()))
        tree.add(pos1())
        assertEquals(tree.size, 1)
        assertTrue(tree.contains(pos1()))

        tree.add(pos2())
        assertEquals(tree.size, 2)
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos1()))

        tree.add(pos1())
        assertEquals(tree.size, 2)
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos1()))

        tree.remove(pos1())
        assertEquals(tree.size, 1)
        assertTrue(tree.contains(pos2()))
        assertFalse(tree.contains(pos1()))

        tree.remove(pos1())
        assertEquals(tree.size, 1)
        assertTrue(tree.contains(pos2()))
        assertFalse(tree.contains(pos1()))

        tree.remove(pos2())
        assertEquals(tree.size, 0)
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(BlockPos(pos1())))
    }

    @Test
    fun add() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))

        assertTrue(tree.add(pos1()))
        assertTrue(tree.contains(pos1()))
        assertFalse(tree.add(pos1()))
        assertTrue(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))

        assertTrue(tree.add(pos2()))
        assertTrue(tree.contains(pos2()))
        assertFalse(tree.add(pos2()))
        assertTrue(tree.contains(pos1()))
        assertTrue(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))

        assertTrue(tree.add(pos3()))
        assertTrue(tree.contains(pos3()))
        assertFalse(tree.add(pos3()))
        assertTrue(tree.contains(pos1()))
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))
    }

    @Test
    fun addAll() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))

        tree.addAll(listOf(pos1(), pos2(), pos3()))

        assertTrue(tree.contains(pos1()))
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))
    }

    @Test
    fun clear() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        assertEquals(tree.size, 0)
        tree.addAll(listOf(pos1(), pos2(), pos3()))
        assertEquals(tree.size, 3)
        tree.clear()
        assertEquals(tree.size, 0)
        tree.clear()
        assertEquals(tree.size, 0)
    }

    @Test
    operator fun iterator() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        val itr = tree.iterator()
        assertFalse(itr.hasNext())
        tree.add(pos1())
        assertFalse(itr.hasNext())
        val itr2 = tree.iterator()
        assertTrue(itr2.hasNext())
        assertEquals(itr2.next(), pos1())
        assertFalse(itr2.hasNext())
        tree.add(pos2())
        val itr3 = tree.iterator()
        assertTrue(itr3.hasNext())
        val itrSet = setOf(itr3.next(), itr3.next())
        assertFalse(itr3.hasNext())
        assertEquals(itrSet, setOf(pos1(), pos2()))
    }

    @Test
    fun remove() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        tree.addAll(listOf(pos1(), pos2(), pos3()))

        assertTrue(tree.contains(pos1()))
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))

        tree.remove(pos2())

        assertTrue(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))

        tree.remove(pos1())

        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))

        tree.remove(pos3())

        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))
        assertTrue(tree.isEmpty())
    }

    @Test
    fun removeAll() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        tree.addAll(listOf(pos1(), pos2()))
        assertEquals(tree.size, 2)
        assertFalse(tree.isEmpty())
        assertTrue(tree.removeAll(listOf(pos1(), pos3())))
        assertEquals(tree.size, 1)
        assertFalse(tree.removeAll(listOf(pos1())))
        assertEquals(tree.size, 1)
        assertTrue(tree.removeAll(listOf(pos1(), pos2())))
        assertTrue(tree.isEmpty())
    }

    @Test
    fun retainAll() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        tree.addAll(listOf(pos1(), pos2()))
        // tree has 1 and 2
        assertEquals(tree.size, 2)
        assertFalse(tree.isEmpty())

        assertTrue(tree.retainAll(listOf(pos1(), pos3())))
        // tree has 1
        assertEquals(tree.size, 1)
        assertTrue(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))
        assertTrue(tree.retainAll(listOf(pos2())))
        // tree is empty
        assertTrue(tree.isEmpty())
    }

    @Test
    fun contains() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))
        tree.addAll(listOf(pos1(), pos2(), pos3()))
        assertTrue(tree.contains(pos1()))
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))
        tree.remove(pos1())
        assertFalse(tree.contains(pos1()))
        assertTrue(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))
        tree.remove(pos2())
        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertTrue(tree.contains(pos3()))
        tree.remove(pos3())
        assertFalse(tree.contains(pos1()))
        assertFalse(tree.contains(pos2()))
        assertFalse(tree.contains(pos3()))
    }

    @Test
    fun containsAll() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        tree.add(pos1())
        assertTrue(tree.containsAll(listOf(pos1())))
        assertFalse(tree.containsAll(listOf(pos1(), pos2())))
        assertFalse(tree.containsAll(listOf(pos3(), pos2())))
        tree.add(pos2())
        assertTrue(tree.containsAll(listOf(pos1(), pos2())))
        assertFalse(tree.containsAll(listOf(pos3(), pos2())))
        tree.add(pos3())
        assertTrue(tree.containsAll(listOf(pos1(), pos2())))
        assertTrue(tree.containsAll(listOf(pos3(), pos2())))
    }

    @Test
    fun isEmpty() = tripleBlockTest { tree, pos1, pos2, pos3 ->
        assertTrue(tree.isEmpty())
        tree.add(pos1())
        assertFalse(tree.isEmpty())
        tree.add(pos2())
        assertFalse(tree.isEmpty())
        tree.remove(pos1())
        assertFalse(tree.isEmpty())
        tree.remove(pos2())
        assertTrue(tree.isEmpty())
    }

    private fun tripleBlockTest(testLogic: (
        tree: BlockTree, pos1: () -> BlockPos, pos2: () -> BlockPos, pos3: () -> BlockPos
        ) -> Unit
    ) {
        val tries = 1
        val rng = Random(System.currentTimeMillis())
        testLogic(
            BlockTree(),
            // { BlockPos(rng.nextInt(), rng.nextInt(), rng.nextInt()) },
            // { BlockPos(rng.nextInt(), rng.nextInt(), rng.nextInt()) },
            // { BlockPos(rng.nextInt(), rng.nextInt(), rng.nextInt()) }
            { BlockPos(12, -124, 9238) },
            { BlockPos(2131, 2, -148) },
            { BlockPos(-2147, -167, -543) }
        )
    }
}
