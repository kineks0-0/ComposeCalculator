package io.github.kineks.composecalculator.ui.layout

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine

@LayoutScopeMarker
@Immutable
class TheLayoutScopeInstance(
    private val columnScope: ColumnScope?, private val rowScope: RowScope?
) : TheLayoutScope {

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier {
        columnScope?.apply {
            return this@align.align(alignment)
        }
        return this
    }

    override fun Modifier.align(alignment: Alignment.Vertical): Modifier {
        rowScope?.apply {
            return this@align.align(alignment)
        }
        return this
    }

    override fun Modifier.alignBy(
        alignmentLineBlock: (Measured) -> Int
    ): Modifier {
        columnScope?.apply {
            return this@alignBy.alignBy(alignmentLineBlock)
        }
        rowScope?.apply {
            return this@alignBy.alignBy(alignmentLineBlock)
        }
        return this
    }

    override fun Modifier.alignBy(alignmentLine: HorizontalAlignmentLine): Modifier {
        rowScope?.apply {
            return this@alignBy.alignBy(alignmentLine)
        }
        return this
    }

    override fun Modifier.alignByBaseline(): Modifier {
        rowScope?.apply {
            return this@alignByBaseline.alignByBaseline()
        }
        return this
    }

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier {
        columnScope?.apply {
            return this@alignBy.alignBy(alignmentLine)
        }
        return this
    }

    @Stable
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
        val modifier = this
        columnScope?.apply {
            return modifier.weight(weight, fill)
        }
        rowScope?.apply {
            return modifier.weight(weight, fill)
        }
        return modifier
    }


}

/**
 * Scope for the children of [TheLayout].
 */
@LayoutScopeMarker
@Immutable
interface TheLayoutScope {

    @Stable
    fun Modifier.align(alignment: Alignment.Horizontal): Modifier

    @Stable
    fun Modifier.align(alignment: Alignment.Vertical): Modifier

    @Stable
    fun Modifier.alignBy(
        alignmentLineBlock: (Measured) -> Int
    ): Modifier

    @Stable
    fun Modifier.alignBy(alignmentLine: HorizontalAlignmentLine): Modifier

    @Stable
    fun Modifier.alignByBaseline(): Modifier

    @Stable
    fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier

    @Stable
    fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier

}


/***
 * 如果当前是横屏则执行: Modifier
 */
@Stable
fun Modifier.isHorizontal(doWork: Modifier.() -> Modifier) = composed {
    if (isHorizontal()) doWork(this)
    else this
}

/***
 * 如果当前不是横屏则执行: Modifier
 */
@Stable
fun Modifier.isNotHorizontal(doWork: Modifier.() -> Modifier) = composed {
    if (!isHorizontal()) doWork(this)
    else this
}