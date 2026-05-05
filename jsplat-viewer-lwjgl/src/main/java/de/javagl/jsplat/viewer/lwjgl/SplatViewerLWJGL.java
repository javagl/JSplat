/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jsplat.viewer.lwjgl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.viewer.AbstractSplatViewer;
import de.javagl.jsplat.viewer.BufferUtils;
import de.javagl.jsplat.viewer.SplatViewer;

/**
 * Implementation of a {@link SplatViewer} based on LWJGL.
 * 
 * NOTE: This class is pretty much tailored for the vertex- and fragment shader
 * from https://github.com/limacv/GaussianSplattingViewer. Some variable names
 * have been aligned with these shaders for consistency, and details of the
 * buffer contents have to match the expectations of these shaders as well.
 * There is not much overlap between this class and the "host"/renderer
 * implementation of GaussianSplattingViewer, but credits to
 * https://github.com/limacv/GaussianSplattingViewer for offering a shader that
 * can easily be fed with splat data and integrated into other renderers.
 */
public class SplatViewerLWJGL extends AbstractSplatViewer implements SplatViewer
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(SplatViewerLWJGL.class.getName());

    /**
     * A direct float buffer for up to 3 elements
     */
    private final FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(3);

    /**
     * The rendering component
     */
    private AWTGLCanvas canvas;

    /**
     * Whether this renderer is already initialized
     */
    private boolean initialized = false;

    /**
     * Whether the size of the rendering component has changed, and a view setup
     * is required
     */
    private boolean viewSetupRequired = true;

    /**
     * The ID for the shader program
     */
    private int program;

    // ------------------------------------------------------------------------
    // The uniform locations, using the names from the vertex shader
    /**
     * mat4
     */
    private int view_matrix_Location;

    /**
     * mat4
     */
    private int projection_matrix_Location;

    /**
     * vec3
     */
    private int hfovxy_focal_Location;

    /**
     * vec3
     */
    private int cam_pos_Location;

    /**
     * int
     */
    private int sh_dim_Location;

    /**
     * float
     */
    private int scale_modifier_Location;

    /**
     * int
     */
    private int render_mod_Location;

    // ------------------------------------------------------------------------

    /**
     * The vertex array object
     */
    private int vao;

    /**
     * The element buffer object for the indices of the two faces of a splat
     */
    private int indicesEBO;

    /**
     * The vertex buffer object for the four vertices of a splat
     */
    private int positionsVBO;

    /**
     * The splat data that is currently rendered
     */
    private final SplatData splatData;

    /**
     * Whether the content of the splat data was modified
     */
    private volatile boolean splatContentModified = true;

    /**
     * Whether the first sorting pass for a modified set of splats is currently
     * pending, and the current sort order should not be used for rendering.
     */
    private volatile boolean firstSortPending = false;

    // ------------------------------------------------------------------------
    // The uniforms for the vertex shader

    /**
     * The spherical harmonics dimensions AS NEEDED BY THE SHADER, for the
     * <code>sh_dim</code> uniform. This means that it is the
     * {@link Splat#getShDimensions()} multiplied by 3.
     */
    private int shDimForShader;

    /**
     * The scale modifier, for the <code>scale_modifier</code> uniform
     */
    private final float scaleModifier = 1.0f;

    /**
     * The render mode, for the <code>render_mod</code> uniform.
     */
    private final int renderMode = 4;

    // ------------------------------------------------------------------------

    /**
     * Creates a new instance
     */
    SplatViewerLWJGL()
    {
        Runnable sortDoneCallback = () ->
        {
            firstSortPending = false;
            getRenderComponent().repaint();
        };
        this.splatData = new SplatData(sortDoneCallback);
        createCanvas();
    }

    /**
     * Create the main GL canvas
     */
    private void createCanvas()
    {
        canvas = new AWTGLCanvas()
        {
            /**
             * Serial UID
             */
            private static final long serialVersionUID = 5564898904227409392L;

            @Override
            public void initGL()
            {
                GL.createCapabilities();
                //GLUtil.setupDebugMessageCallback(System.out);
                performInitGL();
            }

            @Override
            public void paintGL()
            {
                performRender();
                swapBuffers();
            }

            @Override
            public void repaint()
            {
                SwingUtilities.invokeLater(this::render);
            }
        };

        canvas.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent event)
            {
                triggerViewSetup();
                canvas.repaint();
            }

            private void triggerViewSetup()
            {
                viewSetupRequired = true;
            }
        });

    }

    @Override
    protected List<? extends Splat> getSplats()
    {
        return splatData.getSplats();
    }

    @Override
    public Component getRenderComponent()
    {
        return canvas;
    }

    /**
     * Set up the viewport for the current render component
     */
    private void setupView()
    {
        if (!initialized)
        {
            return;
        }
        Component c = getRenderComponent();
        int width = c.getWidth();
        int height = c.getHeight();
        glViewport(0, 0, width, height);
        viewSetupRequired = false;
    }

    /**
     * Initialize the shaders and the shader program
     */
    private void performInitGL()
    {
        initProgram();
        initUniformLocations();
        initSingleSplatData();

        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        int maxSsboSize = glGetInteger(GL_MAX_SHADER_STORAGE_BLOCK_SIZE);
        logger.fine("Maximum Ssbo size: " + maxSsboSize);

        initialized = true;
        setupView();
    }

    /**
     * Initialize the shader program
     */
    private void initProgram()
    {
        program = glCreateProgram();

        // Create, compile, and attach the vertex shader
        String vertexShaderSource = readResourceAsStringUnchecked(
            SplatViewerLWJGL.class, "/vertexShaderSource.glsl");
        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexShaderSource);
        glCompileShader(vertexShaderID);

        int vertexCompileStatus =
            glGetShaderi(vertexShaderID, GL_COMPILE_STATUS);
        if (vertexCompileStatus == GL_FALSE)
        {
            int infoLogLength =
                glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH);
            String infoLog = glGetShaderInfoLog(vertexShaderID, infoLogLength);
            throw new RuntimeException(infoLog);
        }
        glAttachShader(program, vertexShaderID);

        // Create, compile, and attach the fragment shader
        String fragmentShaderSource = readResourceAsStringUnchecked(
            SplatViewerLWJGL.class, "/fragmentShaderSource.glsl");
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentShaderSource);
        glCompileShader(fragmentShaderID);

        int fragmentCompileStatus =
            glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS);
        if (fragmentCompileStatus == GL_FALSE)
        {
            int infoLogLength =
                glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH);
            String infoLog =
                glGetShaderInfoLog(fragmentShaderID, infoLogLength);
            throw new RuntimeException(infoLog);
        }
        glAttachShader(program, fragmentShaderID);

        // Link the program
        glLinkProgram(program);
        int linkStatus = glGetProgrami(program, GL_LINK_STATUS);
        if (linkStatus == GL_FALSE)
        {
            int infoLogLenth = glGetProgrami(program, GL_INFO_LOG_LENGTH);
            String infoLog = glGetProgramInfoLog(program, infoLogLenth);
            throw new RuntimeException("Program linking failed: " + infoLog);
        }

        // Validate the program
        glValidateProgram(program);
        int validateStatus = glGetProgrami(program, GL_VALIDATE_STATUS);
        if (validateStatus == GL_FALSE)
        {
            int infoLogLenth = glGetProgrami(program, GL_INFO_LOG_LENGTH);
            String infoLog = glGetProgramInfoLog(program, infoLogLenth);
            throw new RuntimeException("Program validation failed: " + infoLog);
        }
    }

    /**
     * Initialize all uniform locations of the program
     */
    private void initUniformLocations()
    {
        glUseProgram(program);

        this.view_matrix_Location =
            glGetUniformLocation(program, "view_matrix");
        this.projection_matrix_Location =
            glGetUniformLocation(program, "projection_matrix");
        this.hfovxy_focal_Location =
            glGetUniformLocation(program, "hfovxy_focal");
        this.cam_pos_Location = glGetUniformLocation(program, "cam_pos");
        this.sh_dim_Location = glGetUniformLocation(program, "sh_dim");
        this.scale_modifier_Location =
            glGetUniformLocation(program, "scale_modifier");
        this.render_mod_Location = glGetUniformLocation(program, "render_mod");
    }

    /**
     * Initialize the GL data for a single splat
     */
    private void initSingleSplatData()
    {
        // Create the EBO for the indices
        IntBuffer indices = BufferUtils.createDirectBuffer(new int[]
        { 0, 1, 2, 0, 2, 3 });
        indicesEBO = glCreateBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesEBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Create the VBO for the positions
        FloatBuffer positions = BufferUtils.createDirectBuffer(new float[]
        { -1f, 1f, 1f, 1f, 1f, -1f, -1f, -1f });
        positionsVBO = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionsVBO);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

        // Create the VAO
        vao = glCreateVertexArrays();
        glBindVertexArray(vao);
        int positionAttributeLocation = 0;
        glVertexAttribPointer(positionAttributeLocation, 2, GL_FLOAT, false, 0,
            0);
        glEnableVertexAttribArray(positionAttributeLocation);
        glBindVertexArray(0);
    }

    @Override
    public void setSplats(List<? extends Splat> splats)
    {
        addPreRenderCommand(() -> 
        {
            splatData.setSplats(splats);
            splatContentModified = true;
            firstSortPending = true;
        });
    }

    @Override
    public void addSplats(List<? extends Splat> splats)
    {
        addPreRenderCommand(() -> 
        {
            splatData.addSplats(splats);
            splatContentModified = true;
            firstSortPending = true;
        });
    }

    @Override
    public void addSplatLists(List<? extends List<? extends Splat>> splatLists)
    {
        addPreRenderCommand(() -> 
        {
            splatData.addSplatLists(splatLists);
            splatContentModified = true;
            firstSortPending = true;
        });
    }

    @Override
    public void removeSplats(List<? extends Splat> splats)
    {
        addPreRenderCommand(() -> 
        {
            splatData.removeSplats(splats);
            splatContentModified = true;
            firstSortPending = true;
        });
    }

    @Override
    public void clearSplats()
    {
        addPreRenderCommand(() -> 
        {
            splatData.clearSplats();
            splatContentModified = true;
            firstSortPending = true;
        });
    }

    @Override
    public void updateSplats()
    {
        splatContentModified = true;
        triggerRepaint();
    }

    /**
     * Trigger a repaint
     */
    private void triggerRepaint()
    {
        Component c = getRenderComponent();
        if (c != null)
        {
            c.repaint();
        }
    }

    /**
     * Performs the actual rendering
     */
    private void performRender()
    {
        if (viewSetupRequired)
        {
            setupView();
        }
        processPreRenderCommands();

        splatData.validateStructures();

        if (splatContentModified)
        {
            splatData.validateGaussianData();;
            splatContentModified = false;
        }
        CompoundList<Splat> splats = splatData.getSplats();
        int shDegree = splatData.getShDegree();
        int gaussianDataSsbo = splatData.getGaussianDataSsbo();
        int gaussianOrderSsbo = splatData.getGaussianOrderSsbo();
        int numSplats = splats.size();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        if (numSplats == 0)
        {
            glClear(GL_COLOR_BUFFER_BIT);
            return;
        }
        
        FloatBuffer viewMatrix = obtainCurrentViewMatrixBuffer();
        splatData.validateGaussianOrderCpu(viewMatrix);
        if (firstSortPending)
        {
            logger.fine("Not rendering, first sort pending");
            return;
        }
        else 
        {
            logger.fine("Rendering, first sort NOT pending");
        }
       
        splatData.validateGaussianOrderGpu();

        int shDimensions = Splats.dimensionsForDegree(shDegree);
        shDimForShader = shDimensions * 3;

        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(program);

        // Set the uniforms
        glUniform1f(scale_modifier_Location, scaleModifier);
        glUniform1i(render_mod_Location, renderMode);
        glUniform1i(sh_dim_Location, shDimForShader);

        // Set the camera uniforms
        updateCameraData();

        // Bind the required arrays and buffers, and draw the splats
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesEBO);

        int gaussian_data_Binding = 0;
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, gaussian_data_Binding,
            gaussianDataSsbo);

        int gaussian_order_Binding = 1;
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, gaussian_order_Binding,
            gaussianOrderSsbo);

        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, numSplats);

        // Unbind and wrap up
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        glBindVertexArray(0);

        glFinish();
    }

    /**
     * Update all uniforms that are associated with the camera and view
     * configuration.
     */
    private void updateCameraData()
    {
        glUseProgram(program);

        FloatBuffer viewMatrixBuffer = obtainCurrentViewMatrixBuffer();
        glUniformMatrix4fv(view_matrix_Location, false, viewMatrixBuffer);

        FloatBuffer camPos = obtainCurrentEyePositionBuffer();
        glUniform3fv(cam_pos_Location, camPos);

        FloatBuffer projectionMatrixBuffer =
            obtainCurrentProjectionMatrixBuffer();
        glUniformMatrix4fv(projection_matrix_Location, false,
            projectionMatrixBuffer);

        FloatBuffer focalBuffer = obtainCurrentFocalBuffer();
        glUniform3fv(hfovxy_focal_Location, focalBuffer);
    }

    /**
     * Returns a buffer that stores the data for the <code>hfovxy_focal</code>
     * uniform.
     * 
     * @return The buffer
     */
    private FloatBuffer obtainCurrentFocalBuffer()
    {
        Component comp = getRenderComponent();
        int width = comp.getWidth();
        int height = comp.getHeight();

        float fovDegY = getCameraFovDegY();
        float fovRad = (float) Math.toRadians((fovDegY));
        float htany = (float) Math.tan(fovRad / 2);
        float htanx = htany / height * width;
        float focal = height / (2 * htany);

        floatBuffer3.put(0, htanx);
        floatBuffer3.put(1, htany);
        floatBuffer3.put(2, focal);
        return floatBuffer3.slice();
    }

}
