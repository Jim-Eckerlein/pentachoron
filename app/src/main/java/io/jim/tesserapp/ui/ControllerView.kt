package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.controllers.cameraDistanceController
import io.jim.tesserapp.ui.controllers.rotationController
import io.jim.tesserapp.ui.controllers.translationController


/**
 * This view contains controls related to a graphics view or a controlled geometry.
 * But it does not host the graphics view instance itself.
 *
 * Before the seek bar can actually send data, you must call [control] **once**.
 * By wrapping the [ControllerView] and [GraphicsView] into a [ControlledGraphicsContainerView],
 * this is done automatically.
 */
class ControllerView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        findViewById<SeekBar>(R.id.seekerRotationQ).isEnabled = false
        findViewById<SeekBar>(R.id.seekerTranslationQ).isEnabled = false
    }

    /**
     * Instantiate controllers piping values from seek bars to [graphicsView] and
     * the [SharedRenderData.controlledGeometry].
     */
    fun control(graphicsView: GraphicsView) {

        // Control render grid options:
        findViewById<Switch>(R.id.renderOptionGrid).also {
            // Set render grid option to current checked state:
            graphicsView.renderGrid = it.isChecked
        }.setOnCheckedChangeListener { _, isChecked ->
            // Update the render grid option every times the checked state changes:
            graphicsView.renderGrid = isChecked
        }

        graphicsView.sharedRenderData.controlledGeometry.also {

            // Camera distance:
            cameraDistanceController(graphicsView.sharedRenderData, this)

            // X-Rotation:
            rotationController(
                    context,
                    findViewById(R.id.seekerRotationX),
                    findViewById(R.id.valueRotationX)
            ) { rotation ->
                it.rotation.x = rotation
            }

            // Y-Rotation:
            rotationController(
                    context,
                    findViewById(R.id.seekerRotationY),
                    findViewById(R.id.valueRotationY)
            ) { rotation ->
                it.rotation.y = rotation
            }

            // Z-Rotation:
            rotationController(
                    context,
                    findViewById(R.id.seekerRotationZ),
                    findViewById(R.id.valueRotationZ)
            ) { rotation ->
                it.rotation.z = rotation
            }

            // Q-Rotation:
            rotationController(
                    context,
                    findViewById(R.id.seekerRotationQ),
                    findViewById(R.id.valueRotationQ)
            ) { rotation ->
                it.rotation.q = rotation
            }

            // X-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationX),
                    findViewById(R.id.valueTranslationX)
            ) { translation ->
                it.rotation.x = translation
            }

            // Y-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationY),
                    findViewById(R.id.valueTranslationY)
            ) { translation ->
                it.rotation.y = translation
            }

            // Z-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationZ),
                    findViewById(R.id.valueTranslationZ)
            ) { translation ->
                it.rotation.z = translation
            }

            // Q-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationQ),
                    findViewById(R.id.valueTranslationQ)
            ) { translation ->
                it.rotation.q = translation
            }

        }

    }

}
