#include <jni.h>

#include <GLES2/gl2.h>

#include <vector/Vector.h>
#include <transform/Multiplication.h>
#include <transform/Rotation.h>
#include <transform/Translation.h>
#include <color/Rgb.h>
#include <transform/View.h>
#include <transform/Perspective.h>

#ifdef __cplusplus
extern "C" {
#endif

using namespace fmath;

namespace {
    
    static const std::size_t VECTORS_PER_VERTEX = 2;
    
    /**
     * Vertex, containing a position and a color.
     * Memory is laid out so that the instances can be uploaded directly to GL.
     */
    struct Vertex {
        Vector3d<float> const position;
        float const w;
        Rgb<float> const color;
        float const alpha;
        
        Vertex(
                Vector3d<float> const position,
                Rgb<float> const color
        ) : position{position}, w{1.0f}, color{color}, alpha{1.0f} {}
    };
}

/**
 * Create a model matrix according to transform parameters.
 */
static auto modelMatrix(
        double const rotationX,
        double const rotationY,
        double const rotationZ,
        double const rotationQ,
        double const translationX,
        double const translationY,
        double const translationZ,
        double const translationQ
) -> Matrix<double, 5> {
    return transformChain<double, 5>(
            rotation<double, 5>(RotationPlane::AROUND_X, rotationX),
            rotation<double, 5>(RotationPlane::AROUND_Y, rotationY),
            rotation<double, 5>(RotationPlane::AROUND_Z, rotationZ),
            rotation<double, 5>(RotationPlane::XQ, rotationQ),
            translation(vector4d(
                    translationX,
                    translationY,
                    translationZ,
                    translationQ
            ))
    );
} ;

/**
 * Upload the view matrix.
 */
JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_uploadViewMatrix(
        JNIEnv *const,
        jobject const,
        jint const uniformLocation,
        jdouble const distance,
        jdouble const aspectRatio,
        jdouble const horizontalRotation,
        jdouble const verticalRotation
) -> void {
    auto const matrix = view<float>(static_cast<const float>(horizontalRotation),
            static_cast<const float>(verticalRotation), static_cast<const float>(distance),
            static_cast<const float>(aspectRatio));
    glUniformMatrix4fv(uniformLocation, 1, GL_FALSE,
            reinterpret_cast<const GLfloat *>(matrix.coefficients.data()));
}

/**
 * Upload the projection matrix.
 */
JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_uploadProjectionMatrix(
        JNIEnv *const,
        jobject const,
        jint const uniformLocation
) -> void {
    auto const matrix = perspective<float>(0.1f, 100.0f);
    glUniformMatrix4fv(uniformLocation, 1, GL_FALSE,
            reinterpret_cast<const GLfloat *>(matrix.coefficients.data()));
}

/**
 * Draw a single geometry.
 */
JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_drawGeometry(
        JNIEnv *const env,
        jobject const,
        jobject const positionBuffer,
        jdoubleArray const transformArray,
        jint const color,
        jboolean const isFourDimensional
) -> void {
    
    // Get transform array, containing rotation and translation in a double-array:
    const auto transform = env->GetDoubleArrayElements(transformArray, nullptr);
    
    // Construct geometry model matrix.
    // Each entry of the transform array holds x/y/z/q rotation/translation.
    // See the Kotlin implementation of 'Transform' to checkout the layout.
    auto const matrix = modelMatrix(
            transform[0],
            transform[1],
            transform[2],
            transform[3],
            transform[4],
            transform[5],
            transform[6],
            transform[7]
    );
    
    env->ReleaseDoubleArrayElements(transformArray, transform, 0);
    
    // Get the double buffer containing the position data.
    // Every 4 doubles result in a 4D position.
    auto const positionCounts =
            static_cast<std::size_t>(env->GetDirectBufferCapacity(positionBuffer)) / 4;
    auto const positions =
            reinterpret_cast<double *>(env->GetDirectBufferAddress(positionBuffer));
    
    std::vector<Vertex> vertices;
    vertices.reserve(positionCounts);
    
    for (auto const i : range(positionCounts)) {
        
        auto const position = vector4d(
                positions[i * 4 + 0],
                positions[i * 4 + 1],
                positions[i * 4 + 2],
                positions[i * 4 + 3]
        ) * matrix;
        
        auto const visualized = isFourDimensional ? Vector3d<double>{[position](Dimension const i) {
            return position[i] / position.q();
        }}
                                                  : vector3d(position.x(), position.y(),
                        position.z());
        
        vertices.push_back(Vertex{
                static_cast<Vector3d<float>>(visualized),
                decodeRgb<float>(color)
        });
    }
    
    // Actual buffer has already been bound on Kotlin side.
    glBufferData(GL_ARRAY_BUFFER,
            vertices.size() * sizeof(Vertex),
            vertices.data(),
            GL_DYNAMIC_DRAW);
    
    glDrawArrays(GL_LINES, 0, static_cast<GLsizei>(vertices.size() * VECTORS_PER_VERTEX));
}

#ifdef __cplusplus
}
#endif
